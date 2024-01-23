package com.choreo.SpringHTMX;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class SecureResourceController {

    //This method is used to extract the access token from the ID token to the fronend for the purpose of calling the backend API

    @GetMapping("/secured")
    public String secured(Authentication authentication) {

        DefaultOidcUser userDetails = (DefaultOidcUser) authentication.getPrincipal();

        String token = userDetails.getIdToken().getTokenValue();

        // Create an HTML table with the token value
        String htmlTable = "<table><tr><th>Access Token</th></tr><tr><td>" + token + "</td></tr></table>";

        return htmlTable;
    }

}