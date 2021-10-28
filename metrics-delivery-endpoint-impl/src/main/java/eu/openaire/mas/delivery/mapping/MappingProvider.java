package eu.openaire.mas.delivery.mapping;

import java.util.Set;

public interface MappingProvider {

    PrometheusMetricMeta get(String resourceId, String metricId) throws MappingNotFoundException;
    
    Set<String> listMetrics(String resourceId) throws MappingNotFoundException;

    Set<String> listResources();
}
