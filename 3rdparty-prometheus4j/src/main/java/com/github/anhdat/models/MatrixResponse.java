package com.github.anhdat.models;

import java.util.List;
import java.util.Map;

public class MatrixResponse {
    String status;
    MatrixData data;

    static class MatrixData {
        String resultType;
        List<MatrixResult> result;
        
        public String getResultType() {
            return resultType;
        }
        public List<MatrixResult> getResult() {
            return result;
        }
    }

    static class MatrixResult {
        Map<String, String> metric;
        List<List<Float>> values;

        @Override
        public String toString() {
            return String.format(
                "metric: %s\nvalues: %s",
                metric.toString(),
                values == null ? "" : values.toString()
            );
        }

        public Map<String, String> getMetric() {
            return metric;
        }

        public List<List<Float>> getValues() {
            return values;
        }
    }

    public String getStatus() {
        return status;
    }

    public MatrixData getData() {
        return data;
    }

}
