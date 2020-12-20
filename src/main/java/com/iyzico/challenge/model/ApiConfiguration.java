package com.iyzico.challenge.model;

public class ApiConfiguration {
    private String baseUrl;
    private String apiKey;
    private String secretKey;

    public ApiConfiguration() {
    }

    public ApiConfiguration(String baseUrl, String apiKey, String secretKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
