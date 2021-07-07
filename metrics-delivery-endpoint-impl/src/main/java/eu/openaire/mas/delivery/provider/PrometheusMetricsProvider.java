package eu.openaire.mas.delivery.provider;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.github.anhdat.PrometheusApiClient;
import com.github.anhdat.models.VectorResponse;

import eu.openaire.mas.delivery.MetricEntry;
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
    
    @PostConstruct
    public void initialize() {
        this.prometheusClient = new PrometheusApiClient(prometheusServerLocation);
    }
    
    @Override
    public MetricEntry deliver(String groupId, String metricId, String from, String to) {
        PrometheusMetricMeta meta = mappingProvider.get(groupId, metricId);
        if (meta!=null) {
            try {
                // FIXME it is just a sample, include "from" and "to" params in querying
                VectorResponse resp = prometheusClient.query(meta.getQuery());
                if (STATUS_SUCCESS.equals(resp.getStatus())) {
                    return new MetricEntry(groupId, metricId, resp.getData());    
                } else {
                    throw new RuntimeException(String.format("invalid status: %, full response: %s",
                            resp.getStatus(), resp));
                }
            } catch (IOException e) {
                throw new RuntimeException("unexpected error occurred while communicating with prometheus server", e);
            }
               
        } else {
            throw new NoSuchElementException(
                    String.format("unable to find query mappings for "
                            + "group: %s and metric: %s", groupId, metricId));
        }
    }

    @Override
    public Set<String> list(String groupId) {
        return mappingProvider.listMetrics(groupId);
    }
    
}
