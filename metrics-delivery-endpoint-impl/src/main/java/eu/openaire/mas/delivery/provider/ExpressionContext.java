package eu.openaire.mas.delivery.provider;

import com.github.anhdat.PrometheusApiClient;

/**
 * Context for SpEL expressions executed by PrometheusMetricsProvider.
 */
class ExpressionContext {
    private PrometheusApiClient prometheusClient;

    ExpressionContext(PrometheusApiClient prometheusClient) {
	this.prometheusClient = prometheusClient;
    }
}
