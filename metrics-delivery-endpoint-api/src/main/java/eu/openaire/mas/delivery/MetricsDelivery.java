package eu.openaire.mas.delivery;

/**
 * Metrics delivery API.
 * 
 * @author mhorst
 *
 */
public interface MetricsDelivery {

    MetricEntry deliver(String groupId, String metricId);

    IdList list(String groupId);
}
