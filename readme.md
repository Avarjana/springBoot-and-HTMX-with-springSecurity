# SpringHTMX


## Description

This project demonstrates how to secure spring boot application using spring security. The frontend is built using htmx.
The identify provider used on this is Asgardeo (https://piraveenaparalogarajah.medium.com/secure-your-spring-boot-application-with-asgardeo-3e90e9f9aed4)


## Installation
You can start the spring boot application which will start the server at port 8080. You can access the application using http://localhost:8080

* Visit http://localhost:8080/home which will redirect you to the login page
* Perform the login using the credentials (In this case, a user should be created in Asgardeo)
* After successful login, you will be redirected to the home page
* A button named 'Get Access Token' will be displayed on the home page
* This button will invoke resource named 'secured' in SecuredResourceController which will return the active access token

