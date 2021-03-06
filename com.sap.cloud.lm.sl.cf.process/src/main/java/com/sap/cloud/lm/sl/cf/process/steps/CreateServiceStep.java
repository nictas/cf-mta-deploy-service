package com.sap.cloud.lm.sl.cf.process.steps;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.cloudfoundry.client.lib.CloudControllerClient;
import org.cloudfoundry.client.lib.CloudControllerException;
import org.cloudfoundry.client.lib.CloudOperationException;
import org.cloudfoundry.client.lib.CloudServiceBrokerException;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;

import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudServiceExtended;
import com.sap.cloud.lm.sl.cf.core.cf.clients.ServiceWithAlternativesCreator;
import com.sap.cloud.lm.sl.cf.core.cf.services.ServiceOperationType;
import com.sap.cloud.lm.sl.cf.core.exec.MethodExecution;
import com.sap.cloud.lm.sl.cf.core.exec.MethodExecution.ExecutionState;
import com.sap.cloud.lm.sl.cf.process.Constants;
import com.sap.cloud.lm.sl.cf.process.helpers.ExceptionMessageTailMapper;
import com.sap.cloud.lm.sl.cf.process.helpers.ExceptionMessageTailMapper.CloudComponents;
import com.sap.cloud.lm.sl.cf.process.message.Messages;

@Named("createServiceStep")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateServiceStep extends ServiceStep {

    @Inject
    private ServiceWithAlternativesCreator.Factory serviceCreatorFactory;

    @Override
    protected MethodExecution<String> executeOperation(DelegateExecution execution, CloudControllerClient controllerClient,
                                                       CloudServiceExtended service) {
        return createService(execution, controllerClient, service);
    }

    private MethodExecution<String> createService(DelegateExecution context, CloudControllerClient client, CloudServiceExtended service) {
        getStepLogger().info(Messages.CREATING_SERVICE_FROM_MTA_RESOURCE, service.getName(), service.getResourceName());

        try {
            MethodExecution<String> createServiceMethodExecution = createCloudService(context, client, service);
            getStepLogger().debug(Messages.SERVICE_CREATED, service.getName());
            return createServiceMethodExecution;
        } catch (CloudOperationException e) {
            processServiceCreationFailure(context, service, e);
        }

        return new MethodExecution<>(null, ExecutionState.FINISHED);
    }

    private MethodExecution<String> createCloudService(DelegateExecution context, CloudControllerClient client,
                                                       CloudServiceExtended service) {

        if (serviceExists(service, client)) {
            getStepLogger().info(com.sap.cloud.lm.sl.cf.core.message.Messages.SERVICE_ALREADY_EXISTS, service.getName());
            return new MethodExecution<>(null, ExecutionState.FINISHED);
        }
        if (service.isUserProvided()) {
            return createUserProvidedService(client, service);
        }
        return createManagedService(context, client, service);
    }

    private MethodExecution<String> createUserProvidedService(CloudControllerClient client, CloudServiceExtended service) {
        client.createUserProvidedService(service, service.getCredentials());
        return new MethodExecution<>(null, ExecutionState.FINISHED);
    }

    private boolean serviceExists(CloudServiceExtended cloudServiceExtended, CloudControllerClient client) {
        return client.getService(cloudServiceExtended.getName(), false) != null;
    }

    private MethodExecution<String> createManagedService(DelegateExecution context, CloudControllerClient client,
                                                         CloudServiceExtended service) {
        return serviceCreatorFactory.createInstance(getStepLogger())
                                    .createService(client, service, StepsUtil.getSpaceId(context));
    }

    private void processServiceCreationFailure(DelegateExecution context, CloudServiceExtended service, CloudOperationException e) {
        if (!service.isOptional()) {
            String detailedDescription = MessageFormat.format(Messages.ERROR_CREATING_SERVICE, service.getName(), service.getLabel(),
                                                              service.getPlan(), e.getDescription());
            if (e.getStatusCode() == HttpStatus.BAD_GATEWAY) {
                StepsUtil.setServiceOffering(context, Constants.VAR_SERVICE_OFFERING, service.getLabel());
                throw new CloudServiceBrokerException(e.getStatusCode(), e.getStatusText(), detailedDescription);
            }
            throw new CloudControllerException(e.getStatusCode(), e.getStatusText(), detailedDescription);
        }
        getStepLogger().warn(MessageFormat.format(Messages.COULD_NOT_EXECUTE_OPERATION_OVER_OPTIONAL_SERVICE, service.getName()), e,
                             ExceptionMessageTailMapper.map(configuration, CloudComponents.SERVICE_BROKERS, service.getLabel()));
    }

    @Override
    protected List<AsyncExecution> getAsyncStepExecutions(ExecutionWrapper execution) {
        return Collections.singletonList(new PollServiceCreateOrUpdateOperationsExecution(getServiceOperationGetter(),
                                                                                          getServiceProgressReporter()));
    }

    @Override
    protected ServiceOperationType getOperationType() {
        return ServiceOperationType.CREATE;
    }

    @Override
    protected String getStepErrorMessageAdditionalDescription(DelegateExecution context) {
        String offering = StepsUtil.getServiceOffering(context);
        return ExceptionMessageTailMapper.map(configuration, CloudComponents.SERVICE_BROKERS, offering);
    }

}
