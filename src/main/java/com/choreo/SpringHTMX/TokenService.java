package com.choreo.SpringHTMX;

import com.choreo.SpringHTMX.models.FederatedToken;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.security.oauth2.client.registration.asgardeo.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.asgardeo.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.asgardeo.issuer-uri}")
    private String tokenEndpoint;

    // Store federated tokens
    private List<FederatedToken> federated_tokens;

    @Autowired
    public TokenService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public List<FederatedToken> getFederated_tokens() {

        return federated_tokens;
    }

    public void setFederated_tokens(List<FederatedToken> federated_tokens) {

        this.federated_tokens = federated_tokens;
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
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {

            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                    tokenEndpoint,
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
