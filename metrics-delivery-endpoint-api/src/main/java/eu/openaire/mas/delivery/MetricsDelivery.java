package eu.openaire.mas.delivery;

import java.util.Map;

/**
 * Metrics delivery API.
 * 
 * @author mhorst
 *
 */
public interface MetricsDelivery {

    MetricEntry deliver(String resourceId, String metricId, Long timestamp);

    ItemList<String> list(String resourceId);

    MetricMetadata describe(String resourceId, String metricId);

    Map<String, MetricMetadata> describeResource(String resourceId);

    ItemList<String> listResources();
}
