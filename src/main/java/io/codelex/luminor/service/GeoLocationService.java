package io.codelex.luminor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeoLocationService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${geoapify.api.key}")
    private String apiKey;

    public GeoLocationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String resolveCountryCode(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "unknown";
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://api.geoapify.com/v1/ipinfo").queryParam("ip", ip).queryParam("apiKey", apiKey).toUriString();

        try {
            String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

            return parseCountryCode(response);
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String parseCountryCode(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("country").path("iso_code").asText("unknown");
        } catch (Exception e) {
            return "unknown";
        }
    }
}
