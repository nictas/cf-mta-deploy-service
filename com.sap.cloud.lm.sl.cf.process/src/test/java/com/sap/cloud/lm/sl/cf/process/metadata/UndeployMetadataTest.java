package com.sap.cloud.lm.sl.cf.process.metadata;

import com.sap.cloud.lm.sl.cf.process.Constants;
import com.sap.cloud.lm.sl.cf.web.api.model.OperationMetadata;

public class UndeployMetadataTest extends MetadataBaseTest {

    @Override
    protected OperationMetadata getMetadata() {
        return UndeployMetadata.getMetadata();
    }

    @Override
    protected String getDiagramId() {
        return Constants.UNDEPLOY_SERVICE_ID;
    }

    @Override
    protected String[] getVersions() {
        return new String[] { Constants.SERVICE_VERSION_1_0 };
    }

    @Override
    protected String[] getParametersIds() {
        return new String[] {
            // @formatter:off
                Constants.PARAM_DELETE_SERVICES,
                Constants.PARAM_DELETE_SERVICE_BROKERS,
                Constants.PARAM_MTA_ID,
                Constants.PARAM_NO_RESTART_SUBSCRIBED_APPS,
                Constants.PARAM_NO_FAIL_ON_MISSING_PERMISSIONS,
                Constants.PARAM_ABORT_ON_ERROR,
            // @formatter:on
        };
    }
}