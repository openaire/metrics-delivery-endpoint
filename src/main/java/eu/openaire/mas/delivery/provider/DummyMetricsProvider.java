package eu.openaire.mas.delivery.provider;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import eu.openaire.mas.delivery.KPIEntry;

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

    public KPIEntry deliver(String groupId, String metricId) {
        return new KPIEntry(groupId, metricId, String.format(template, counter.incrementAndGet()));
    }

}
