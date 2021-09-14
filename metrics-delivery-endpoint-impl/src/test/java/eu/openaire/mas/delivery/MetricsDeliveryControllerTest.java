package eu.openaire.mas.delivery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.openaire.mas.delivery.provider.MetricsProvider;

/**
 * @author mhorst
 *
 */
@ExtendWith(MockitoExtension.class)
public class MetricsDeliveryControllerTest {

    private MetricsDeliveryController metricsDeliveryController = new MetricsDeliveryController();
    
    @Mock
    private MetricsProvider metricsProvider;
    
    @BeforeEach
    public void init() {
        metricsDeliveryController.setMetricsProvider(metricsProvider);
    }
    
    @Test
    public void whenFamilyIdAndNameProvided_thenStaticValueIsReturned() {
        // given
        String familyId = "someFamId";
        String name = "someName";
        float value = 1;
        MetricEntry kpiEntry = new MetricEntry(familyId, name, value);        
        Mockito.when(metricsProvider.deliver(familyId, name, null, null)).thenReturn(kpiEntry);
        
        // execute
        MetricEntry result = metricsDeliveryController.deliver(familyId, name, null, null);
        
        // assert
        assertNotNull(result);
        assertEquals(familyId, result.getResourceId());
        assertEquals(name, result.getKpiId());
        assertEquals(value, result.getValue());
    }
}
