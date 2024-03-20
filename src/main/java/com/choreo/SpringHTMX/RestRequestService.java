package com.choreo.SpringHTMX;

import java.io.IOException;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public class RestRequestService {

    private RestTemplate restTemplate;
    private String accessToken;
    private String refreshToken;
    private TokenService tokenService;

    public RestRequestService(RestTemplate restTemplate, TokenService tokenService, String accessToken,
            String refreshToken) {
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @SuppressWarnings("null")
    public ResponseEntity<String> send(String invocationURL, HttpMethod method, HttpEntity<?> requestEntity, HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Set access token in Authorization header

        HttpEntity<?> finalRequest = new HttpEntity<>(requestEntity.getBody(), headers);

        try {

            System.out.println("Initial invocation triggered");

            return restTemplate.exchange(invocationURL, method, finalRequest, String.class);

        } catch (HttpClientErrorException.Unauthorized ex) {
            // HTTP 401 Unauthorized, try to refresh the access token

            System.out.println("Catch block triggered: Unauthorized");

            accessToken = tokenService.getNewAccessToken(refreshToken, restTemplate);

            if (accessToken == null) {
                // If unable to refresh token, redirect user to login
                // Assuming redirect is a method to handle login redirect

                System.out.println("Redirecting to login page...");

                redirectUserToLogin(request, response);
            
            }

            // Retry the API call after token refresh
            headers.setBearerAuth(accessToken); // Update access token in the headers
            finalRequest = new HttpEntity<>(requestEntity.getBody(), headers);
            return restTemplate.exchange(invocationURL, method, finalRequest, String.class);

        } catch (HttpServerErrorException ex) {
            // Handle other HTTP errors
            // You can customize this according to your requirements

            // add a log statement here
            System.out.println("Error: " + ex.getStatusCode());
            return new ResponseEntity<>(ex.getStatusCode());
        }
    }

    private void redirectUserToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the URL for the login endpoint
        String loginUrl = "/login"; // Assuming "/login" is the URL for your login endpoint

        // Construct the full login URL including the current request URI as the redirect URL
        String redirectUrl = request.getRequestURI();

        // Redirect the user to the login page
        response.sendRedirect(loginUrl + "?redirect=" + redirectUrl);
    }
}
