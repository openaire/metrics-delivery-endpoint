package eu.openaire.mas.delivery.provider;

import java.util.Set;

import eu.openaire.mas.delivery.MetricEntry;

/**
 * Metrics delivery module. 
 * 
 * @author mhorst
 *
 */
public interface MetricsProvider {

    /**
     * Delivers metric details.
     */
    MetricEntry deliver(String resourceId, String metricId, String from, String to);
    
    /**
     * Lists metric identifiers for a given resource.
     */
    Set<String> list(String resourceId);

    /**
     * List all known resource identifiers
     */
    Set<String> listResources();
}
