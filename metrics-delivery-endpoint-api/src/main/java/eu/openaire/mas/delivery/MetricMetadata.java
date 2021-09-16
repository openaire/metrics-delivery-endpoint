package eu.openaire.mas.delivery;

public class MetricMetadata {

    public enum MetricKind {
	STATE, EVENT
    }

    private final String name;
    private final String description;
    private final String unit;
    private final MetricKind type;

    public MetricMetadata(String name, String description, String unit, MetricKind type) {
	this.name = name;
	this.description = description;
	this.unit = unit;
	this.type = type;
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public String getUnit() {
	return unit;
    }
    public MetricKind getType() {
	return type;
    }
}
