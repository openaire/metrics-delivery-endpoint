package eu.openaire.mas.delivery.provider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import eu.openaire.mas.delivery.MetricEntry;
import eu.openaire.mas.delivery.mapping.MappingProvider;

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
        return new HashSet<String>(Arrays.asList(new String[] {"dummy"}));
    }

}
