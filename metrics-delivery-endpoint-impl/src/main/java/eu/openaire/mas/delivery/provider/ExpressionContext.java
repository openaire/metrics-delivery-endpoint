package eu.openaire.mas.delivery.provider;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import com.github.anhdat.PrometheusApiClient;
import com.github.anhdat.models.MatrixResponse;

/**
 * Context for SpEL expressions executed by PrometheusMetricsProvider.
 */
class ExpressionContext {
    private PrometheusApiClient prometheusClient;

    ExpressionContext(PrometheusApiClient prometheusClient) {
	this.prometheusClient = prometheusClient;
    }

    /**
     * Sums values of a series provided periodically for which there is another series provided
     * in the same exposition which is different (usually because it represents a timestamp) for each
     * new value of the series to sum.
     *
     * @param countsSeries the series to sum
     * @param stampsSeries the series of timestamps
     * @param from initial timestamp to go back to
     * @return sum of values from the countsSeries matching each distinct value of the stampsSeries
     */
    public float sumPeriods(String countsSeries, String stampsSeries, long from) throws IOException {
	long now = System.currentTimeMillis() / 1000;
	String range = "[" + (now - from) +"s]";
	MatrixResponse rc = prometheusClient.queryMatrix(countsSeries+range, ""+now);
	MatrixResponse rs = prometheusClient.queryMatrix(stampsSeries+range, ""+now);
	List<List<Float>> stamps = rs.getData().getResult().get(0).getValues();
	HashSet<Float> uniqueStamps = new HashSet<>();
	HashSet<Float> uniqueStampsStamps = new HashSet<>();
	for (List<Float> pair : stamps) {
	    if (uniqueStamps.add(pair.get(1))) {
		uniqueStampsStamps.add(pair.get(0));
	    }
	}
	float sum = 0;
	List<List<Float>> counts = rc.getData().getResult().get(0).getValues();
	for (List<Float> pair : counts) {
	    if (uniqueStampsStamps.contains(pair.get(0))) {
		sum += pair.get(1);
	    }
	}
	return sum;
    }
}
