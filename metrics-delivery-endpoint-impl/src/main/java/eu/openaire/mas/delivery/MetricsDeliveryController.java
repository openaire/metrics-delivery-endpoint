package eu.openaire.mas.delivery;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/metrics/{resourceId}/{metricId}/value")
    public MetricEntry deliver(
            @PathVariable(value = "resourceId") String resourceId,
            @PathVariable(value = "metricId") String metricId,
	    @RequestParam(value = "time", required = false) Long time) {
        return metricsProvider.deliver(resourceId, metricId, null, null);
    }

    @Override
    @GetMapping("/metrics/{resourceId}/{metricId}")
    public MetricMetadata describe(
            @PathVariable(value = "resourceId") String resourceId,
            @PathVariable(value = "metricId") String metricId) {
        return metricsMetadataProvider.describe(resourceId, metricId);
    }

    @Override
    @GetMapping("/metrics/{resourceId}")
    public Map<String, MetricMetadata> describeResource(
	    @PathVariable(value = "resourceId") String resourceId) {
        return metricsMetadataProvider.describeAll(resourceId);
    }

    @Override
    @GetMapping("/ids/metrics/{resourceId}")
    public ItemList<String> list(
            @PathVariable(value = "resourceId") String resourceId) {
        Set<String> result = metricsProvider.list(resourceId);
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
