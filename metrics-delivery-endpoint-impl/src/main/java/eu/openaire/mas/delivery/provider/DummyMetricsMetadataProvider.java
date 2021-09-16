package eu.openaire.mas.delivery.provider;

import java.util.ArrayList;
import java.util.List;

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
    public List<MetricMetadata> describeAll(String groupId) {
	List<MetricMetadata> result = new ArrayList<>();

	for (String metricId : metricsProvider.list(groupId)) {
	    result.add(describe(groupId, metricId));
	}

	return result;
    }
}
