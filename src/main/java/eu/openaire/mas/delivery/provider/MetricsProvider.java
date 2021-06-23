package eu.openaire.mas.delivery.provider;

import eu.openaire.mas.delivery.KPIEntry;

/**
 * Metrics delivery module. 
 * 
 * @author mhorst
 *
 */
public interface MetricsProvider {

    KPIEntry deliver(String groupId, String metricId);
}
