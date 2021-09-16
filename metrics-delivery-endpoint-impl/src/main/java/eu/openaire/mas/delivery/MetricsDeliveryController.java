package eu.openaire.mas.delivery;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import eu.openaire.mas.delivery.provider.MetricsProvider;
import eu.openaire.mas.delivery.provider.MetricsMetadataProvider;

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

    @Autowired
    private MetricsMetadataProvider metricsMetadataProvider;

    @Override
    @GetMapping("/metrics/{resourceId}/{kpiId}/value")
    public MetricEntry deliver(
            @PathVariable(value = "resourceId") String groupId,
            @PathVariable(value = "kpiId") String metricId) {
        return metricsProvider.deliver(groupId, metricId, null, null);
    }

    @Override
    @GetMapping("/metrics/{resourceId}/{kpiId}")
    public MetricMetadata describe(
            @PathVariable(value = "resourceId") String resourceId,
            @PathVariable(value = "kpiId") String kpiId) {
        return metricsMetadataProvider.describe(resourceId, kpiId);
    }

    @Override
    @GetMapping("/metrics/{resourceId}")
    public ItemList<MetricMetadata> describeResource(
	    @PathVariable(value = "resourceId") String resourceId) {
        return new ItemList<>(metricsMetadataProvider.describeAll(resourceId));
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
