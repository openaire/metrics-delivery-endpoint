package eu.openaire.mas.delivery;

public class MetricMetadata {

    public enum MetricKind {
	STATE, EVENT
    }

    private final String name;
    private final String description;
    private final String unit;
    private final MetricKind type;
	private final String eoscMetricDefinitionId;

    public MetricMetadata(String name, String description, String unit, MetricKind type, String eoscMetricDefinitionId) {
	this.name = name;
	this.description = description;
	this.unit = unit;
	this.type = type;
	this.eoscMetricDefinitionId = eoscMetricDefinitionId;
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

    /**
     * Returns the identifier of the metric definition corresponding to the metric
     * in the EOSC accounting service.
     */
	public String getEoscMetricDefinitionId() {
		return eoscMetricDefinitionId;
	}
}
