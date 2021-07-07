package com.github.anhdat.models;

import java.util.List;
import java.util.Map;

public class KeyValResponse {
    String status;
    List<Map<String, String>> data;
    
    public String getStatus() {
        return status;
    }

    public List<Map<String, String>> getData() {
        return data;
    }
    
}
