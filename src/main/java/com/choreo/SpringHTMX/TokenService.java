package com.choreo.SpringHTMX;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class TokenService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public TokenService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @SuppressWarnings("null")
    public String getRefreshToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());
        if (authorizedClient != null && authorizedClient.getRefreshToken() != null) {
            return authorizedClient.getRefreshToken().getTokenValue();
        }
        return null;
    }

    public String getAccessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());
        if (authorizedClient != null) {
            return authorizedClient.getAccessToken().getTokenValue();
        }
        return null;
    }

    public String getNewAccessToken(String refreshToken, RestTemplate restTemplate) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken);
        map.add("client_id", "hT4QHkwOVzBfdHm2eXrdFPJ4_bQa");
        map.add("client_secret", "Q4h43bwbGtWFzL7o5zusgdkBKAruEbDRdt_f7RAoorka");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {

            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                    "https://api.asgardeo.io/t/sarindas/oauth2/token",
                    HttpMethod.POST,
                    request,
                    TokenResponse.class);

            return response.getBody().getAccess_token();
            
        } catch (HttpClientErrorException.BadRequest ex) {
            System.out.println("Client error: " + ex.getStatusCode() + " - " + ex.getStatusText());
            System.out.println("Error response: " + ex.getResponseBodyAsString());

            // Handle the 400 Bad Request error appropriately, e.g., prompt the user to
            // re-authenticate.
            return null;
        }

    }
}
