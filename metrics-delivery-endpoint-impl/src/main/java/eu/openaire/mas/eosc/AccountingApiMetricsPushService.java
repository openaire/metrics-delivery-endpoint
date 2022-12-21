package eu.openaire.mas.eosc;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import eu.eosc.accounting_system.client.api.MetricApi;
import eu.eosc.accounting_system.client.invoker.ApiClient;
import eu.eosc.accounting_system.client.invoker.auth.Authentication;
import eu.eosc.accounting_system.client.invoker.auth.OAuth;
import eu.eosc.accounting_system.client.model.InlineResponse2011;
import eu.eosc.accounting_system.client.model.MetricRequest;
import eu.openaire.mas.delivery.MetricMetadata;
import eu.openaire.mas.delivery.provider.MetricsMetadataProvider;
import eu.openaire.mas.delivery.provider.MetricsProvider;

/**
 * A service which regularly pushes metric values to an EOSC accounting API instance.
 */
@Service
public class AccountingApiMetricsPushService {

    private static Logger log = LoggerFactory.getLogger(AccountingApiMetricsPushService.class);

    @Value("${eosc.accounting-api.base-path}")
    private String basePath;

    @Value("${counter.range.start}")
    private long metricRangeStart;

    @Autowired
    private MetricsProvider metricsProvider;

    @Autowired
    private MetricsMetadataProvider metricsMetadataProvider;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private ApiClient apiClient;
    private MetricApi metricApi;

    @PostConstruct
    public void initialize() {
    	apiClient = new ApiClient(restTemplateBuilder.build());
    	apiClient.setBasePath(basePath);
    	metricApi = new MetricApi(apiClient);
    }

    /**
     * Set the access token to use for the API calls.
     */
    void setAccessToken(String token) {
    	apiClient.setAccessToken(token);
    }

    /**
     * Checks if the API client is configured with the necessary access token.
     *
     * @return <code>true</code> if there is an access token
     */
    private boolean hasAccessToken() {
    	for (Entry<String, Authentication> entry : apiClient.getAuthentications().entrySet()) {
			Authentication auth = entry.getValue();
			if (auth instanceof OAuth) {
				return isNotEmpty(((OAuth) auth).getAccessToken());
			}
		}
		return false;
    }

	@Scheduled(fixedRateString = "${eosc.accounting-api.push-rate}")
	void pushMetrics() {
		if (!hasAccessToken()) {
			log.warn("Access token needed to push metrics");
			return;
		}
		// NOTE: Offsets must be UTC to be accepted by the accounting API
		OffsetDateTime ts = OffsetDateTime.ofInstant(Instant.ofEpochSecond(metricRangeStart), ZoneOffset.UTC);
		OffsetDateTime te = OffsetDateTime.now(ZoneId.ofOffset("", ZoneOffset.UTC));
		Set<String> resIds = metricsProvider.listResources();
		for (String resId : resIds) {
			String instId = metricsMetadataProvider.getEoscInstallationId(resId);
			if (instId == null) {
				log.debug("No installation id for resource '{}'", resId);
				continue;
			}
			Map<String, MetricMetadata> ms = metricsMetadataProvider.describeAll(resId);
			for (Entry<String, MetricMetadata> e : ms.entrySet()) {
				String metricId = e.getKey();
				MetricMetadata metric = e.getValue();
				String mdId = metric.getEoscMetricDefinitionId();
				if (mdId == null) {
					log.debug("No metric definition id for metric '{}/{}'", resId, metricId);
					continue;
				}
				log.debug("eosc ids for {}/{}: {}/{}", resId, metricId, instId, mdId);
				Double mv = (double) metricsProvider.deliver(resId, metricId, te.toEpochSecond()).getValue();
				MetricRequest mr = new MetricRequest()
						.metricDefinitionId(mdId)
						.timePeriodStart(ts)
						.timePeriodEnd(te)
						.value(mv);
				InlineResponse2011 ret = metricApi.accountingSystemInstallationsInstallationIdMetricsPost(instId, mr);
				log.info("Pushed {}/{} = {} => {}", resId, metricId, mv, ret.getMetricId());
			}
		}
	}
}
