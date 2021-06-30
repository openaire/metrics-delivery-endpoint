package eu.openaire.mas.delivery;

/**
 * Metrics delivery API.
 * 
 * @author mhorst
 *
 */
public interface MetricsDelivery {

    MetricEntry deliver(String groupId, String metricId, String from, String to);

    String[] list(String groupId);
    
}
