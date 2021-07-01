package eu.openaire.mas.delivery.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Type;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonFileBasedMappingProviderTest {

    @Test
    public void test_serde() throws Exception {
        
        // given
        String mappingLoc = "eu/openaire/mas/delivery/mapping/test_mappings.json";
        Gson gson = new Gson();
        Type empMapType = new TypeToken<Map<String, Map<String, PrometheusMetricMeta>>>() {}.getType();
        
        // execute
        Map<String, Map<String, PrometheusMetricMeta>> metricsMap = gson.fromJson(
                JsonFileBasedMappingProvider.getResourceFileAsString(mappingLoc), empMapType);
        
        // assert
        assertEquals(2, metricsMap.size());
        
        assertTrue(metricsMap.containsKey("group1"));
        assertTrue(metricsMap.get("group1").containsKey("metric11"));
        assertFalse(metricsMap.get("group1").get("metric11").isCounter());
        assertEquals("query11", metricsMap.get("group1").get("metric11").getQuery());
        assertTrue(metricsMap.get("group1").containsKey("metric12"));
        assertTrue(metricsMap.get("group1").get("metric12").isCounter());
        assertEquals("query12", metricsMap.get("group1").get("metric12").getQuery());
        
        assertTrue(metricsMap.containsKey("group2"));
        assertTrue(metricsMap.get("group2").containsKey("metric21"));
        assertFalse(metricsMap.get("group2").get("metric21").isCounter());
        assertEquals("query21", metricsMap.get("group2").get("metric21").getQuery());
        assertTrue(metricsMap.get("group2").containsKey("metric22"));
        assertTrue(metricsMap.get("group2").get("metric22").isCounter());
        assertEquals("query22", metricsMap.get("group2").get("metric22").getQuery());
    }
}
