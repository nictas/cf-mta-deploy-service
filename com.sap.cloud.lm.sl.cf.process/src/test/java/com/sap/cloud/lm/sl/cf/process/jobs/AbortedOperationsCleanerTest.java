package com.sap.cloud.lm.sl.cf.process.jobs;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.cloud.lm.sl.cf.core.persistence.query.HistoricOperationEventQuery;
import com.sap.cloud.lm.sl.cf.core.persistence.service.HistoricOperationEventService;
import com.sap.cloud.lm.sl.cf.core.persistence.service.OperationService;
import com.sap.cloud.lm.sl.cf.process.flowable.FlowableFacade;

public class AbortedOperationsCleanerTest {

    @Mock
    private HistoricOperationEventService historicOperationEventService;
    @Mock
    private FlowableFacade flowableFacade;
    @Mock
    private OperationService operationService;
    private Supplier<Instant> currentInstantSupplier = () -> Instant.now();
    private AbortedOperationsCleaner abortedOperationsCleaner;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.abortedOperationsCleaner = new AbortedOperationsCleaner(historicOperationEventService,
                                                                     flowableFacade,
                                                                     operationService,
                                                                     currentInstantSupplier);
    }

    @Test
    public void testExecuteWithNoAbortedOperations() {
        HistoricOperationEventQuery query = Mockito.mock(HistoricOperationEventQuery.class, Mockito.RETURNS_SELF);
        Mockito.when(query.list())
               .thenReturn(Collections.emptyList());
        Mockito.when(historicOperationEventService.createQuery())
               .thenReturn(query);

        abortedOperationsCleaner.execute(new Date()); // Passed argument is not used.

        Mockito.verifyNoInteractions(flowableFacade, operationService);
    }

}
