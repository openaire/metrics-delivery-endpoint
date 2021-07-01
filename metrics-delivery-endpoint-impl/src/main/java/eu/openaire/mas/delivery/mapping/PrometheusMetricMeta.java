package eu.openaire.mas.delivery.mapping;

/**
 * Prometheus related metrics metadata.
 * @author mhorst
 *
 */
public class PrometheusMetricMeta {
    
    private final boolean isCounter;
    
    private final String query;

    public PrometheusMetricMeta(boolean isCounter, String query) {
        this.isCounter = isCounter;
        this.query = query;
    }
    
    public boolean isCounter() {
        return isCounter;
    }

    public String getQuery() {
        return query;
    }
    
}
