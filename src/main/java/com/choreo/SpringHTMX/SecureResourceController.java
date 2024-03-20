package com.choreo.SpringHTMX;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController

public class SecureResourceController {

    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public SecureResourceController(TokenService tokenService, OAuth2AuthorizedClientService authorizedClientService) {
        this.tokenService = tokenService;
        this.authorizedClientService = authorizedClientService;
    }

    RestTemplate restTemplate = new RestTemplate();
    TokenService tokenService = new TokenService(authorizedClientService);

    // This method is used to extract the access token from the ID token to the
    // fronend for the purpose of calling the backend API

    @GetMapping("/secured")
    public String secured(OAuth2AuthenticationToken authentication) {

        // String AccessToken = userDetails.getIdToken().getTokenValue();

        String refreshToken = tokenService.getRefreshToken(authentication);

        String accessToken = tokenService.getAccessToken(authentication);

        // Create an HTML table with the token value
        String htmlTable = "<table><tr><th>Access Token</th></tr><tr><td>" + refreshToken + "</td></tr>tr><th>Access Token</th></tr><tr><td>" + accessToken + "</td></tr></table>";

        return htmlTable;
    }

    @GetMapping("/choreoAPI")
    public String choreoAPI(OAuth2AuthenticationToken authentication, HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpHeaders headers = new HttpHeaders();

        // create a request entity to add to send method

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        String refreshToken = tokenService.getRefreshToken(authentication);

        String accessToken = tokenService.getAccessToken(authentication);

        RestRequestService restRequestService = new RestRequestService(restTemplate, tokenService, accessToken,
                refreshToken);

        HttpEntity<String> response1 = restRequestService.send(
                "https://f3ed8d71-af1c-4ab3-8ebd-50f70586fd6c-dev.e1-us-east-azure.choreoapis.dev/appw/proxy/v1/test",
                HttpMethod.GET, requestEntity, request, response);

        // Create an HTML table with the token value
        String htmlTable = "<table><tr><th>Access Token</th></tr><tr><td>" + response1 + "</td></tr></table>";

        return htmlTable;
    }

}