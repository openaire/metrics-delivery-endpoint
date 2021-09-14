package eu.openaire.mas.delivery.provider;

import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.github.anhdat.PrometheusApiClient;
import com.github.anhdat.models.VectorResponse;

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
    
    @PostConstruct
    public void initialize() {
        this.prometheusClient = new PrometheusApiClient(prometheusServerLocation);
    }
    
    @Override
    public MetricEntry deliver(String groupId, String metricId, String from, String to) {
        try {
            PrometheusMetricMeta meta = mappingProvider.get(groupId, metricId);
            if (meta!=null) {
                try {
                    // FIXME it is just a sample, include "from" and "to" params in querying
                    VectorResponse resp = prometheusClient.query(meta.getQuery());
                    if (STATUS_SUCCESS.equals(resp.getStatus())) {
			float value = resp.getData().getResult().get(0).getValue().get(1);
                        return new MetricEntry(groupId, metricId, value);
                    } else {
                        throw new RuntimeException(String.format("invalid status: %, full response: %s",
                                resp.getStatus(), resp));
                    }
                } catch (IOException e) {
                    throw new RuntimeException("unexpected error occurred while communicating with prometheus server", e);
                }
                   
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        String.format("unable to find query mappings for "
                                + "group: %s and metric: %s", groupId, metricId));
            }    
        } catch (MappingNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("maping not found for groupId: '%s' and metricId: '%s'", 
                            groupId, metricId), e);
        }
        
    }

    @Override
    public Set<String> list(String groupId) {
        try {
            return mappingProvider.listMetrics(groupId);
        } catch (MappingNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "maping not found for groupId: " + groupId, e);
        }
    }
    
}
