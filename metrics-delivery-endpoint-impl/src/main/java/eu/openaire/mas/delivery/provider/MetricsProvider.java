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
    MetricEntry deliver(String groupId, String metricId, String from, String to);
    
    /**
     * Lists metric identifiers for a given group.
     */
    Set<String> list(String groupId);

    /**
     * List all known resource identifiers
     */
    Set<String> listResources();
}
