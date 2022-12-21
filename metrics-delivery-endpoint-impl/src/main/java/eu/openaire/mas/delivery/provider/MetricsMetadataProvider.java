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

    /**
     * Returns the EOSC Accounting installation id for a resource
     *
     * @return the installation id, <code>null</code> if not configured
     */
	String getEoscInstallationId(String resourceId);
}
