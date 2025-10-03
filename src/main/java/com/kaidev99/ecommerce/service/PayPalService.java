package com.kaidev99.ecommerce.service;


import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class PayPalService {

    @Value("${paypal.client-id}")
    private String clientId;
    @Value("${paypal.client-secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseUrl() {
        return "sandbox".equals(mode) ? "https://api-m.sandbox.paypal.com" : "https://api-m.paypal.com";
    }

    /**
     * Lấy Access Token từ PayPal
     */
    private String getAccessToken() throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/v1/oauth2/token")
                .post(body)
                .header("Authorization", "Basic " + Credentials.basic(clientId, clientSecret))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JsonNode responseNode = objectMapper.readTree(response.body().string());
            return responseNode.get("access_token").asText();
        }
    }

    /**
     * Tạo một đơn hàng trên PayPal
     */
    public JsonNode createOrder(double totalAmount, String currency) throws IOException {
        String accessToken = getAccessToken();

        String payload = String.format(
                "{\"intent\":\"CAPTURE\",\"purchase_units\":[{\"amount\":{\"currency_code\":\"%s\",\"value\":\"%.2f\"}}]}",
                currency, totalAmount
        );

        RequestBody body = RequestBody.create(payload, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/v2/checkout/orders")
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readTree(response.body().string());
        }
    }

    /**
     * Ghi nhận (capture) thanh toán cho một đơn hàng
     */
    public JsonNode captureOrder(String orderId) throws IOException {
        String accessToken = getAccessToken();

        RequestBody body = RequestBody.create("", null); // Body rỗng

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/v2/checkout/orders/" + orderId + "/capture")
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readTree(response.body().string());
        }
    }
}