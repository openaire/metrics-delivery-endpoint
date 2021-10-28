package eu.openaire.mas.delivery.provider;

import java.util.Map;

import eu.openaire.mas.delivery.MetricMetadata;

public interface MetricsMetadataProvider {

    /**
     * Returns metric metadata
     */
    MetricMetadata describe(String resourceId, String metricId);

    /**
     * Returns metric metadata for all metrics of a resource
     */
    Map<String, MetricMetadata> describeAll(String resourceId);
}
