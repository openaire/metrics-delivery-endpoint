package eu.openaire.mas.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import eu.openaire.mas.delivery.provider.MetricsProvider;

/**
 * RESTful metrics delivery controller.
 * 
 * @author mhorst
 *
 */
@RestController
public class MetricsDeliveryController {

    @Autowired
    private MetricsProvider metricsProvider;

    @GetMapping("/metrics/{groupId}/{metricId}")
    public KPIEntry deliver(@PathVariable(value = "groupId") String groupId,
            @PathVariable(value = "metricId") String metricId) {
        return metricsProvider.deliver(groupId, metricId);
    }
    
    public void setMetricsProvider(MetricsProvider metricsProvider) {
        this.metricsProvider = metricsProvider;
    }

}
