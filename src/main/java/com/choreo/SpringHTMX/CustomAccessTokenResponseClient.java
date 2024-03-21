package com.choreo.SpringHTMX;

import com.choreo.SpringHTMX.models.FederatedToken;
import com.choreo.SpringHTMX.models.TokenResponseModel;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class CustomAccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    @Value("${spring.security.oauth2.client.registration.asgardeo.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.provider.asgardeo.issuer-uri}")
    private String tokenEndpoint;

    @Autowired
    TokenService tokenService;

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {

        OAuth2AuthorizationExchange authorizationExchange = authorizationGrantRequest.getAuthorizationExchange();
        OAuth2AuthorizationResponse authorizationResponse = authorizationExchange.getAuthorizationResponse();

        // Make a call to the token endpoint to exchange authorization code for access token
        // Customize this part according to your OAuth2 provider's token endpoint
        // Here's a basic example using RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", authorizationResponse.getCode());
        requestBody.add("redirect_uri", authorizationExchange.getAuthorizationRequest().getRedirectUri());
        requestBody.add("client_id", clientId);
        requestBody.add("state", authorizationExchange.getAuthorizationRequest().getState());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Make a POST request to the token endpoint
        ResponseEntity<TokenResponseModel> responseEntity = restTemplate.postForEntity(
                tokenEndpoint,
                requestEntity,
                TokenResponseModel.class
        );

        Map<String, Object> additionalParams = new HashMap<>();
        additionalParams.put("id_token", responseEntity.getBody().getId_token());

        if (responseEntity.getBody().getFederated_tokens() != null) {
            // We can store the federated tokens from here.
            tokenService.setFederated_tokens(responseEntity.getBody().getFederated_tokens());
            // Uncomment the following print statement to get an idea of the content
             for (FederatedToken federatedToken: responseEntity.getBody().getFederated_tokens()) {
                System.out.println(federatedToken.toString());
            }
        }

        // Retrieve the token response from the response entity
        OAuth2AccessTokenResponse tokenResponse =
                OAuth2AccessTokenResponse.withToken(
                        responseEntity.getBody().getAccess_token())
                        .tokenType(OAuth2AccessToken.TokenType.BEARER)
                        .refreshToken(responseEntity.getBody().getRefresh_token())
                        .additionalParameters(additionalParams)
                        .scopes(Arrays.stream(responseEntity.getBody().getScope().split("\\s+"))
                                .collect(Collectors.toSet()))
                        .build();

        return tokenResponse;
    }
}
