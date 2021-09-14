package eu.openaire.mas.delivery.mapping;

import java.util.Set;

public interface MappingProvider {

    PrometheusMetricMeta get(String groupId, String metricId) throws MappingNotFoundException;
    
    Set<String> listMetrics(String groupId) throws MappingNotFoundException;

    Set<String> listGroups();
}
