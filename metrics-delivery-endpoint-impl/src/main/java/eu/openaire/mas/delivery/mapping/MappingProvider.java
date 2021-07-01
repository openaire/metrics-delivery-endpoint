package eu.openaire.mas.delivery.mapping;

import java.util.NoSuchElementException;
import java.util.Set;

public interface MappingProvider {

    PrometheusMetricMeta get(String groupId, String metricId) throws NoSuchElementException;
    
    Set<String> listMetrics(String groupId) throws NoSuchElementException;
}
