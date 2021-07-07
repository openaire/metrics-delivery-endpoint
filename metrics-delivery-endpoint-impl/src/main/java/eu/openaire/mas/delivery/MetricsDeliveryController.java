package eu.openaire.mas.delivery;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.openaire.mas.delivery.provider.MetricsProvider;

/**
 * RESTful metrics delivery controller.
 * 
 * @author mhorst
 *
 */
@RestController
public class MetricsDeliveryController implements MetricsDelivery {

    @Autowired
    private MetricsProvider metricsProvider;

    @GetMapping("/metrics/{groupId}/{metricId}")
    public MetricEntry deliver(
            @PathVariable(value = "groupId") String groupId,
            @PathVariable(value = "metricId") String metricId,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to) {
        return metricsProvider.deliver(groupId, metricId, from, to);
    }
    
    @GetMapping("/metrics/{groupId}")
    public String[] list(
            @PathVariable(value = "groupId") String groupId) {
        Set<String> result = metricsProvider.list(groupId);
        return result != null ? result.toArray(new String[result.size()]) : new String[0];
    }
    
    public void setMetricsProvider(MetricsProvider metricsProvider) {
        this.metricsProvider = metricsProvider;
    }

}