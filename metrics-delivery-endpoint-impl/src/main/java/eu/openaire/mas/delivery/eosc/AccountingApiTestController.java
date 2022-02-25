package eu.openaire.mas.delivery.eosc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.eosc.accounting_system.client.api.MetricApi;
import eu.eosc.accounting_system.client.api.MetricDefinitionApi;
import eu.eosc.accounting_system.client.invoker.ApiClient;
import eu.eosc.accounting_system.client.model.InlineResponse201;
import eu.eosc.accounting_system.client.model.InlineResponse2011;
import eu.eosc.accounting_system.client.model.MetricDefinitionRequest;
import eu.eosc.accounting_system.client.model.MetricDefinitionResponse;
import eu.eosc.accounting_system.client.model.MetricRequest;
import eu.openaire.mas.delivery.MetricMetadata;
import eu.openaire.mas.delivery.provider.MetricsMetadataProvider;
import eu.openaire.mas.delivery.provider.MetricsProvider;

/**
 * A controller for running tests of the EOSC accounting API using the data
 * and interfaces of MDE.
 */
@RestController
public class AccountingApiTestController {

    private ApiClient apiClient;
    private MetricDefinitionApi metricDefinitionApi;
    private MetricApi metricApi;
    private Map<String, String> eoscIds = new HashMap<>();

    @Autowired
    private MetricsProvider metricsProvider;

    @Autowired
    private MetricsMetadataProvider metricsMetadataProvider;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @PostConstruct
    public void initialize() {
	apiClient = new ApiClient(restTemplateBuilder.build());
	apiClient.setBasePath("https://acc.devel.argo.grnet.gr");
	metricDefinitionApi = new MetricDefinitionApi(apiClient);
	metricApi = new MetricApi(apiClient);
	eoscIds = readEoscIds();
    }

    @GetMapping("/-test/eosc-ids")
    Map<String, String> getEoscIds() {
	return eoscIds;
    }

    @PostMapping("/-test/register-definitions")
    Map<String, String> registerDefinitions() {
	for (String res : metricsProvider.listResources()) {
	    Map<String, MetricMetadata> metrics = metricsMetadataProvider.describeAll(res);
	    for (String id : metrics.keySet()) {
		MetricMetadata metric = metrics.get(id);
		String unit = metric.getUnit();
		String lcId = id.toLowerCase();
		if (!unit.isEmpty() && !eoscIds.containsKey(lcId)) {
		    String eoscId = registerMetric(id, metric);
		    eoscIds.put(lcId, eoscId);
		}
	    }
	}
	return getEoscIds();
    }

    @PostMapping("/-test/push-metrics")
    Map<String, String> pushMetrics() {
	Map<String, String> ret = new HashMap<>();
	for (String res : metricsProvider.listResources()) {
	    Map<String, MetricMetadata> metrics = metricsMetadataProvider.describeAll(res);
	    for (String id : metrics.keySet()) {
		String lcId = id.toLowerCase();
		if (eoscIds.containsKey(lcId)) {
		    String mid = exportMetric(res, id);
		    ret.put(res+":"+id, mid);
		}
	    }
	}
	return ret;
    }

    private String exportMetric(String res, String id) {
	OffsetDateTime ts = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
	OffsetDateTime te = OffsetDateTime.of(2022, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);;
	Double mv = (double) metricsProvider.deliver(res, id, te.toEpochSecond()).getValue();
	MetricRequest mr = new MetricRequest()
	    .metricDefinitionId(eoscIds.get(id.toLowerCase()))
	    .resourceId(res)
	    .timePeriodStart(ts)
	    .timePeriodEnd(te)
	    .value(mv);
	InlineResponse2011 ret = metricApi.accountingSystemMetricsPost(mr);
	return ret.getMetricId();
    }

    private String registerMetric(String id, MetricMetadata metric) {

	MetricDefinitionRequest md = new MetricDefinitionRequest()
	    .metricName(id)
	    .metricType("aggregated")
	    .unitType(metric.getUnit());
	InlineResponse201 ret = metricDefinitionApi.submitMetricDefinition(md);
	return ret.getMetricDefinitionId();
    }

    private Map<String, String> readEoscIds() {
	Map<String, String> map = new HashMap<>();
	List<MetricDefinitionResponse> ret = metricDefinitionApi.accountingSystemMetricDefinitionGet();
	for (MetricDefinitionResponse res : ret) {
	    map.put(res.getMetricName(), res.getMetricDefinitionId());
	}
	return map;
    }
}
