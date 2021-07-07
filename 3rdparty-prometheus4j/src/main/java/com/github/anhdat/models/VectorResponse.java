package com.github.anhdat.models;

import java.util.List;
import java.util.Map;

public class VectorResponse {
    String status;
    VectorData data;

    static class VectorResult {
        Map<String, String> metric;
        List<Float> value;

        @Override
        public String toString() {
            return String.format(
                "metric: %s\nvalue: %s",
                metric.toString(),
                value == null ? "" : value.toString()
            );
        }

        public Map<String, String> getMetric() {
            return metric;
        }

        public List<Float> getValue() {
            return value;
        }
    }

    static class VectorData {
        String resultType;
        List<VectorResult> result;
        
        public String getResultType() {
            return resultType;
        }
        public List<VectorResult> getResult() {
            return result;
        }
    }

    public String getStatus() {
        return status;
    }

    public VectorData getData() {
        return data;
    }
}
