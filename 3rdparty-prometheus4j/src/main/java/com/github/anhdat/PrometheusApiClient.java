package com.github.anhdat;

import java.io.IOException;

import com.github.anhdat.models.KeyValResponse;
import com.github.anhdat.models.MatrixResponse;
import com.github.anhdat.models.VectorResponse;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PrometheusApiClient {

    private PrometheusRest service;

    public PrometheusApiClient(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        service = retrofit.create(PrometheusRest.class);
    }

    public VectorResponse query(String query) throws IOException {
        return service.query(query, null, null).execute().body();
    }

    public VectorResponse query(String query, String time) throws IOException {
        return service.query(query, time, null).execute().body();
    }

    public MatrixResponse queryMatrix(String query, String time) throws IOException {
        return service.queryMatrix(query, time, null).execute().body();
    }

    public VectorResponse query(String query, String time, String timeout) throws IOException {
        return service.query(query, time, timeout).execute().body();
    }

    public MatrixResponse queryRange(String query, String start, String end, String step, String timeout) throws IOException {
        return service.queryRange(query, start, end, step, timeout).execute().body();
    }

    public KeyValResponse findSeries(String match, String start, String end) throws IOException {
        return service.findSeries(match, start, end).execute().body();
    }

}
