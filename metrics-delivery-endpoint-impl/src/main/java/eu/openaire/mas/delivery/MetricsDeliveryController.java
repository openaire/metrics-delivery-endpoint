package eu.openaire.mas.delivery;

import java.util.Set;

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
public class MetricsDeliveryController implements MetricsDelivery {

    @Autowired
    private MetricsProvider metricsProvider;

    @GetMapping("/metrics/{resourceId}/{kpiId}")
    @Override
    public MetricEntry deliver(
            @PathVariable(value = "resourceId") String groupId,
            @PathVariable(value = "kpiId") String metricId) {
        return metricsProvider.deliver(groupId, metricId, null, null);
    }
    
    @Override
    @GetMapping("/ids/metrics/{resourceId}")
    public ItemList<String> list(
            @PathVariable(value = "resourceId") String groupId) {
        Set<String> result = metricsProvider.list(groupId);
	return new ItemList<>(result);
    }

    @Override
    @GetMapping("/ids/resources")
    public ItemList<String> listResources() {
	Set<String> resourceIds = metricsProvider.listResources();
	return new ItemList<>(resourceIds);
    }

    public void setMetricsProvider(MetricsProvider metricsProvider) {
        this.metricsProvider = metricsProvider;
    }

}
