package eu.openaire.mas.delivery;

/**
 * Metrics delivery API.
 * 
 * @author mhorst
 *
 */
public interface MetricsDelivery {

    MetricEntry deliver(String groupId, String metricId);

    ItemList<String> list(String groupId);

    ItemList<String> listResources();
}
