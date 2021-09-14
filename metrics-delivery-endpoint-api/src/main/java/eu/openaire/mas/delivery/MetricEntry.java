package eu.openaire.mas.delivery;

/**
 * Represents KPI record.
 * @author mhorst
 *
 */
public class MetricEntry {

    private final String resourceId;

    private final String kpiId;
    
    private final Object value;
    
    public MetricEntry(String resourceId, String kpiId, Object value) {
        super();
        this.resourceId = resourceId;
        this.kpiId = kpiId;
        this.value = value;
    }    
    
    public String getResourceId() {
        return resourceId;
    }

    public String getKpiId() {
        return kpiId;
    }

    public Object getValue() {
        return value;
    }
    
}
