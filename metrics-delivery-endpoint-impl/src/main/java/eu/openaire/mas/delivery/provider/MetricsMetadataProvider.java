package eu.openaire.mas.delivery.provider;

import java.util.List;

import eu.openaire.mas.delivery.MetricMetadata;

public interface MetricsMetadataProvider {

    /**
     * Returns metric metadata
     */
    MetricMetadata describe(String groupId, String metricId);

    /**
     * Returns metric metadata for all metrics in a group
     */
    List<MetricMetadata> describeAll(String groupId);

}
