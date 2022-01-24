package eu.openaire.mas.delivery.provider;

import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.anhdat.PrometheusApiClient;
import com.github.anhdat.models.VectorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import eu.openaire.mas.delivery.MetricEntry;
import eu.openaire.mas.delivery.mapping.MappingNotFoundException;
import eu.openaire.mas.delivery.mapping.MappingProvider;
import eu.openaire.mas.delivery.mapping.PrometheusMetricMeta;

/**
 * Metrics provider implementation relying on the prometheus backend.
 * @author mhorst
 *
 */
@Primary
@Service
public class PrometheusMetricsProvider implements MetricsProvider {

    @Value("${prometheus.server.location}")
    private String prometheusServerLocation;
    
    @Autowired
    private MappingProvider mappingProvider;
    
    private PrometheusApiClient prometheusClient;
    
    private static final String STATUS_SUCCESS = "success";
    private static final String SPEL_PREFIX = "#";
    
    @PostConstruct
    public void initialize() {
        this.prometheusClient = new PrometheusApiClient(prometheusServerLocation);
    }
    
    @Override
    public MetricEntry deliver(String resourceId, String metricId, String from, String to) {
        try {
            PrometheusMetricMeta meta = mappingProvider.get(resourceId, metricId);
            if (meta!=null) {
		return runQuery(resourceId, metricId, meta.getQuery());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        String.format("unable to find query mappings for "
                                + "resource: %s and metric: %s", resourceId, metricId));
            }    
        } catch (MappingNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("maping not found for resourceId: '%s' and metricId: '%s'",
                            resourceId, metricId), e);
        }
        
    }

    private MetricEntry runQuery(String resourceId, String metricId, String query) {
	try {
	    if (query.startsWith(SPEL_PREFIX)) {
		float value = runSpEL(query.substring(SPEL_PREFIX.length()));
		return new MetricEntry(resourceId, metricId, value);
	    }
	    // FIXME it is just a sample, include "from" and "to" params in querying
	    VectorResponse resp = prometheusClient.query(query);
	    if (STATUS_SUCCESS.equals(resp.getStatus())) {
		float value = resp.getData().getResult().get(0).getValue().get(1);
		return new MetricEntry(resourceId, metricId, value);
	    } else {
		throw new RuntimeException(String.format("invalid status: %, full response: %s",
							 resp.getStatus(), resp));
	    }
	} catch (IOException e) {
	    throw new RuntimeException("unexpected error occurred while communicating with prometheus server", e);
	}
    }

    private float runSpEL(String query) {
	SpelExpressionParser parser = new SpelExpressionParser();
	Expression exp = parser.parseExpression(query);
	ExpressionContext ec = new ExpressionContext(prometheusClient);
	return exp.getValue(ec, Float.class);
    }

    @Override
    public Set<String> list(String resourceId) {
        try {
            return mappingProvider.listMetrics(resourceId);
        } catch (MappingNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "maping not found for resourceId: " + resourceId, e);
        }
    }

    @Override
    public Set<String> listResources() {
	return mappingProvider.listResources();
    }
}
