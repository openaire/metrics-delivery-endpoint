package eu.openaire.mas.delivery.mapping;

/**
 * Metric identifier to prometheus metric metadata mapping.
 * @author mhorst
 *
 */
public class MetricMappingEntry extends PrometheusMetricMeta {

    private final String metricId;
    
    public MetricMappingEntry(String metricId, boolean isCounter, String query) {
        super(isCounter, query);
        this.metricId = metricId;
    }

    public String getMetricId() {
        return metricId;
    }

    
}
