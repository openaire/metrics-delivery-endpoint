package eu.openaire.mas.delivery;

import java.util.Map;

/**
 * Information about a single resource and its metrics.
 */
public class ResourceMetadata {

	private String eoscInstallationId;
	private Map<String, MetricMetadata> metrics;

	/**
	 * Returns the metadata for the metrics of the resource.
	 */
	public Map<String, MetricMetadata> getMetrics() {
		return metrics;
	}

	/**
	 * Returns the identifier of the installation corresponding to the resource
	 * in the EOSC accounting service.
	 */
	public String getEoscInstallationId() {
		return eoscInstallationId;
	}
}
