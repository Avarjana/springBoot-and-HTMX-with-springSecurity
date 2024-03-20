package com.choreo.SpringHTMX;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

@Controller
public class LoginController {

	// This method is called when the user visits the "http://localhost:8080/home" link of the SPA.
	
	@GetMapping("/home")
	public String currentUserName(Model model, Authentication authentication) {

		// This information is extracted by processing the ID token received at the time of login from the IDP (This case Asgardeo)
		
		DefaultOidcUser userDetails = (DefaultOidcUser) authentication.getPrincipal();
		model.addAttribute("email", userDetails.getEmail());
		model.addAttribute("givenName", userDetails.getGivenName());

		// Spring security automatically generates a session
		return "home";
	}

	// There is no logout method required in this controller. Spring security will handle it

	@GetMapping("testMapping/*")
	public String wildcardMapping() {
		return "test mapping text";
	}

}

