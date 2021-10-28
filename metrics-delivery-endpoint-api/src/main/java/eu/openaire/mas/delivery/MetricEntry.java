package eu.openaire.mas.delivery;

/**
 * Represents a metric record.
 * @author mhorst
 *
 */
public class MetricEntry {

    private final String resourceId;

    private final String metricId;
    
    private final float value;
    
    public MetricEntry(String resourceId, String metricId, float value) {
        super();
        this.resourceId = resourceId;
        this.metricId = metricId;
        this.value = value;
    }    
    
    public String getResourceId() {
        return resourceId;
    }

    public String getMetricId() {
        return metricId;
    }

    public float getValue() {
        return value;
    }
    
}
