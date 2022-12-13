package eu.openaire.mas.eoscaccounting;

import eu.eosc.accounting_system.client.api.MetricDefinitionApi;
import eu.eosc.accounting_system.client.invoker.ApiClient;
import eu.eosc.accounting_system.client.model.InlineResponse2003;
import eu.eosc.accounting_system.client.model.InlineResponse2004;
import eu.eosc.accounting_system.client.model.InlineResponse2005;
import eu.eosc.accounting_system.client.model.MetricDefinitionResponse;

/**
 * This class is responsible for running manual tests against EOSC accounting API.
 * 
 * @author mhorst
 *
 */
public class EoscAccountingAPIManualTest {

   
    public static void main(String[] args) {
        // setting up the client
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("https://acc.devel.argo.grnet.gr");
        
        // listing available metric definitions
        MetricDefinitionApi metricDefinitionApi = new MetricDefinitionApi(apiClient);
        InlineResponse2005 results = metricDefinitionApi.accountingSystemMetricDefinitionsGet(1, 1000);
        for (MetricDefinitionResponse result : results.getContent()) {
            System.out.println(String.format("metric id: %s", result.getMetricDefinitionId()));
            System.out.println(String.format("metric name: %s", result.getMetricName()));
            System.out.println(String.format("metric type: %s", result.getMetricType()));
            System.out.println(String.format("metric description: %s", result.getMetricDescription()));
            System.out.println(String.format("unit type: %s", result.getUnitType()));
            System.out.println("-----------------------------------------------------------");
        }
        
    }

}
