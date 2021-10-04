package eu.openaire.mas.delivery;

import java.util.Map;

/**
 * Metrics delivery API.
 * 
 * @author mhorst
 *
 */
public interface MetricsDelivery {

    MetricEntry deliver(String groupId, String metricId);

    ItemList<String> list(String groupId);

    MetricMetadata describe(String groupId, String metricId);

    Map<String, MetricMetadata> describeResource(String groupId);

    ItemList<String> listResources();
}
