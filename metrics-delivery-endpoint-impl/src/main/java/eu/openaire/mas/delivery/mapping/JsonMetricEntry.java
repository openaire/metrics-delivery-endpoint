package eu.openaire.mas.delivery.mapping;

public class JsonMetricEntry {

    private String groupId;
    
    private MetricMappingEntry[] mappings;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public MetricMappingEntry[] getMappings() {
        return mappings;
    }

    public void setMappings(MetricMappingEntry[] mappings) {
        this.mappings = mappings;
    } 
}
