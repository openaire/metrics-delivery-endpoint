package eu.openaire.mas.delivery;

/**
 * Represents KPI record.
 * @author mhorst
 *
 */
public class KPIEntry {

    private final String groupId;

    private final String metricId;
    
    private final Object value;
    
    public KPIEntry(String groupId, String metricId, Object value) {
        super();
        this.groupId = groupId;
        this.metricId = metricId;
        this.value = value;
    }    
    
    public String getGroupId() {
        return groupId;
    }

    public String getMetricId() {
        return metricId;
    }

    public Object getValue() {
        return value;
    }
    
}
