package com.jane.tutorials.junit.ui.controllers;

import com.jane.tutorials.junit.security.SecurityConstants;
import com.jane.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="/application-test.properties",
        properties = "server.port=0")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersControllerIntegrationTest {
    @Value("${server.port}")
    int serverPort;

    @LocalServerPort
    int localServerPort;

    @Autowired
    TestRestTemplate testRestTemplate;

    private String authorizationToken;

    @Test
    @DisplayName("User can be created")
    @Order(1)
    void testCreateUser_whenValidUserDetailsProvided_returnCreatedUserDetails() throws JSONException {
//        Arrange
        JSONObject userDetailsJSONObject = new JSONObject();
        userDetailsJSONObject.put("firstName", "Ritu");
        userDetailsJSONObject.put("lastName","Bafna");
        userDetailsJSONObject.put("email","abc@test.com");
        userDetailsJSONObject.put("password", "12345678");
        userDetailsJSONObject.put("repeatPassword", "12345678");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsJSONObject.toString(),headers);

//        Act
        ResponseEntity<UserRest> createdUserDetailsEntity = testRestTemplate.postForEntity("/users",
                request,
                UserRest.class);
        UserRest createdUserDetails = createdUserDetailsEntity.getBody();

//        Assert
        assertEquals(HttpStatus.OK, createdUserDetailsEntity.getStatusCode(), "status code should be OK");
        assertEquals(userDetailsJSONObject.getString("firstName"), createdUserDetails.getFirstName(), "First name is incorrect");
        assertEquals(userDetailsJSONObject.getString("lastName"), createdUserDetails.getLastName(), "Last name is incorrect");
        assertEquals(userDetailsJSONObject.getString("email"), createdUserDetails.getEmail(), "Email is incorrect");
    }

    @Test
    @DisplayName("GET /users require JWT")
    @Order(2)
    void testGetUsers_whenMissingJWT_returns403(){
//        Arrange
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept","application/json");

        HttpEntity request = new HttpEntity(null, httpHeaders);


//        Act
        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<UserRest>>(){
                });

//        Assert
        assertEquals(HttpStatus.FORBIDDEN,
                response.getStatusCode(),
                "Forbidden status code 403 should have been returned.");

    }

    @Test
    @DisplayName("/login works")
    @Order(3)
    void testUserLogin_whenValidCredentialsProvided_returnsJWTInAuthorizationHeader() throws JSONException {
//        Arrange
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "abc@test.com");
        loginCredentials.put("password", "12345678");

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString());

//        Act
        ResponseEntity response = testRestTemplate.postForEntity("/users/login", request, null);

//        Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Response should return a 200 status code");
        authorizationToken = response.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0);

        assertNotNull(authorizationToken,
                "Response should contain Authorization header with token");
        assertNotNull(response.getHeaders().getValuesAsList("UserId").get(0),
                "Response should contain UserId in a response header");


    }

    @Test
    @Order(4)
    @DisplayName("GET /users works")
    void testGetUsers_whenValidJWTTokenProvided_returnsUsers(){
//        Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity request = new HttpEntity<>(headers);

//        Act
        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<UserRest>>() {
                });

//        Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP status ode should be 200");
        assertTrue(response.getBody().size() == 1,
                "There should be exactly one user in the list");

    }

}
