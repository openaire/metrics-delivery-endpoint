package eu.openaire.mas.delivery.provider;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.openaire.mas.delivery.MetricMetadata;
import eu.openaire.mas.delivery.MetricMetadata.MetricKind;

@Service
public class DummyMetricsMetadataProvider implements MetricsMetadataProvider {

    @Autowired
    private MetricsProvider metricsProvider;

    @Override
    public MetricMetadata describe(String groupId, String metricId) {
	return new MetricMetadata(groupId+" "+metricId, "Lorem ipsum...", "attoparsec", MetricKind.STATE);
    }

    @Override
    public Map<String, MetricMetadata> describeAll(String groupId) {
	Map<String, MetricMetadata> result = new HashMap<>();

	for (String metricId : metricsProvider.list(groupId)) {
	    result.put(metricId, describe(groupId, metricId));
	}

	return result;
    }
}
