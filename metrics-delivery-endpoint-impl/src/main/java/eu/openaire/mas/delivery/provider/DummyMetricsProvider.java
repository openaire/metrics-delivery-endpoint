package eu.openaire.mas.delivery.provider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import eu.openaire.mas.delivery.MetricEntry;

/**
 * Embarrasingly simple metrics provider.
 * 
 * @author mhorst
 *
 */
@Service
public class DummyMetricsProvider implements MetricsProvider {

    private static final String template = "measurements count: %s";
    private final AtomicLong counter = new AtomicLong();

    @Override
    public MetricEntry deliver(String groupId, String metricId, String from, String to) {
        return new MetricEntry(groupId, metricId, String.format(template, counter.incrementAndGet()));
    }

    @Override
    public Set<String> list(String groupId) {
        return new HashSet<String>(Arrays.asList(new String[] {"dummy"}));
    }

}
