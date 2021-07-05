package eu.openaire.mas.delivery.provider;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import eu.openaire.mas.delivery.MetricEntry;
import eu.openaire.mas.delivery.mapping.MappingProvider;

/**
 * Metrics provider implementation relying on the prometheus backend.
 * @author mhorst
 *
 */
@Primary
@Service
public class PrometheusMetricsProvider implements MetricsProvider {

    @Autowired
    MappingProvider mappingProvider;
    
    @Override
    public MetricEntry deliver(String groupId, String metricId, String from, String to) {
        return new MetricEntry(groupId, metricId, "value");
    }

    @Override
    public Set<String> list(String groupId) {
        return mappingProvider.listMetrics(groupId);
    }
    
}
