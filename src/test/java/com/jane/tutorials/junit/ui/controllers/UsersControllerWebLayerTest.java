package com.jane.tutorials.junit.ui.controllers;

import com.jane.tutorials.junit.service.UsersService;
import com.jane.tutorials.junit.shared.UserDto;
import com.jane.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.jane.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UsersController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
//@AutoConfigureMockMvc(addFilters = false)
//@MockBean({UsersServiceImpl.class})
public class UsersControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    //@Autowired
    UsersService usersService;

    private UserDetailsRequestModel userDetailsRequestModel;

    @BeforeEach
    void setup() {
        userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("Sergey");
        userDetailsRequestModel.setLastName("Kargopolov");
        userDetailsRequestModel.setEmail("test@test.com");
        userDetailsRequestModel.setPassword("12345678");
    }

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidUserDetailsProvided_returnsCreatedUserDetails() throws Exception {
        // Arrange
        UserDto userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createdUser = new ObjectMapper()
                .readValue(responseBodyAsString, UserRest.class);

        // Assert
        Assertions.assertEquals(userDetailsRequestModel.getFirstName(),
                createdUser.getFirstName(), "The returned user first name is most likely incorrect");

        Assertions.assertEquals(userDetailsRequestModel.getLastName(),
                createdUser.getLastName(), "The returned user last name is incorrect");

        Assertions.assertEquals(userDetailsRequestModel.getEmail(),
                createdUser.getEmail(), "The returned user email is incorrect");

        Assertions.assertFalse(createdUser.getUserId().isEmpty(), "userId should not be empty");

    }

    @Test
    @DisplayName("First name is not empty")
    void testCreateUser_whenFirstNameIsNotProvided_returns400StatusCode() throws Exception {
        // Arrange
        userDetailsRequestModel.setFirstName("");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),
                mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned");


    }

    @Test
    @DisplayName("First name cannot be shorter than 2 characters")
    void testCreateUser_whenFirstNameIsOnlyOneCharacter_returns400StatusCode() throws Exception {
        // Arrange
        userDetailsRequestModel.setFirstName("a");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),
                result.getResponse().getStatus(), "HTTP Status code is not set to 400");
    }
}
