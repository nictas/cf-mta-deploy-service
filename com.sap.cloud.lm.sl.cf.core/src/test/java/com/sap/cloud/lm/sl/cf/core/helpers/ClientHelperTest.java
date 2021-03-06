package com.sap.cloud.lm.sl.cf.core.helpers;

import java.util.UUID;

import org.cloudfoundry.client.lib.CloudControllerClient;
import org.cloudfoundry.client.lib.CloudOperationException;
import org.cloudfoundry.client.lib.domain.CloudMetadata;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.ImmutableCloudMetadata;
import org.cloudfoundry.client.lib.domain.ImmutableCloudOrganization;
import org.cloudfoundry.client.lib.domain.ImmutableCloudSpace;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import com.sap.cloud.lm.sl.cf.core.model.CloudTarget;
import com.sap.cloud.lm.sl.cf.core.util.ApplicationURI;

public class ClientHelperTest {

    private static final String ORG_NAME = "some-organization";
    private static final String SPACE_NAME = "custom-space";
    private static final UUID GUID = UUID.randomUUID();
    private static final String SPACE_ID = "8819b12c-6dde-4338-8530-93b2fba56df6";

    @Mock
    private CloudControllerClient client;

    private ClientHelper clientHelper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientHelper = new ClientHelper(client);
    }

    @Test
    public void testDeleteRoute() {
        String uri = "https://some-route.next.domain";
        clientHelper.deleteRoute(uri);
        ApplicationURI route = new ApplicationURI(uri);
        Mockito.verify(client)
               .deleteRoute(route.getHost(), route.getDomain(), route.getPath());
    }

    @Test
    public void testComputeSpaceId() {
        Mockito.when(client.getSpace(ORG_NAME, SPACE_NAME, false))
               .thenReturn(createCloudSpace(GUID, SPACE_NAME, ORG_NAME));
        String spaceId = clientHelper.computeSpaceId(ORG_NAME, SPACE_NAME);
        Assertions.assertEquals(GUID.toString(), spaceId);
    }

    @Test
    public void testComputeSpaceIdIfSpaceIsNull() {
        Assertions.assertNull(clientHelper.computeSpaceId(ORG_NAME, SPACE_NAME));
    }

    @Test
    public void testComputeTarget() {
        Mockito.when(client.getSpace(Matchers.any(UUID.class)))
               .thenReturn(createCloudSpace(GUID, SPACE_NAME, ORG_NAME));
        CloudTarget target = clientHelper.computeTarget(SPACE_ID);
        Assertions.assertEquals(ORG_NAME, target.getOrganizationName());
        Assertions.assertEquals(SPACE_NAME, target.getSpaceName());
    }

    @Test
    public void testComputeTargetCloudOperationExceptionForbiddenThrown() {
        Mockito.when(client.getSpace(Matchers.any(UUID.class)))
               .thenThrow(new CloudOperationException(HttpStatus.FORBIDDEN));
        Assertions.assertNull(clientHelper.computeTarget(SPACE_ID));
    }

    @Test
    public void testComputeTargetCloudOperationExceptionNotFoundThrown() {
        Mockito.when(client.getSpace(Matchers.any(UUID.class)))
               .thenThrow(new CloudOperationException(HttpStatus.NOT_FOUND));
        Assertions.assertNull(clientHelper.computeTarget(SPACE_ID));
    }

    @Test
    public void testComputeTargetCloudOperationExceptionBadRequestThrown() {
        Mockito.when(client.getSpace(Matchers.any(UUID.class)))
               .thenThrow(new CloudOperationException(HttpStatus.BAD_REQUEST));
        CloudOperationException cloudOperationException = Assertions.assertThrows(CloudOperationException.class,
                                                                                  () -> clientHelper.computeTarget(SPACE_ID));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cloudOperationException.getStatusCode());
    }

    private CloudSpace createCloudSpace(UUID guid, String spaceName, String organizationName) {
        return ImmutableCloudSpace.builder()
                                  .name(spaceName)
                                  .organization(createCloudOrganization(organizationName))
                                  .metadata(createCloudMetadata(guid))
                                  .build();
    }

    private CloudOrganization createCloudOrganization(String organizationName) {
        return ImmutableCloudOrganization.builder()
                                         .name(organizationName)
                                         .build();
    }

    private CloudMetadata createCloudMetadata(UUID guid) {
        return ImmutableCloudMetadata.builder()
                                     .guid(guid)
                                     .build();
    }
}
