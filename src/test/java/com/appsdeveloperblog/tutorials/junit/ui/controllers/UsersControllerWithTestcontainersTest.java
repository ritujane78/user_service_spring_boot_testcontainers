package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
public class UsersControllerWithTestcontainersTest {
    @Container
    @ServiceConnection
    private static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.4.0");
//            .withDatabaseName("photo_app")
//            .withUsername("root")
//            .withPassword("root");

//    @DynamicPropertySource
//    private static void overrideProperties(DynamicPropertyRegistry registry){
//        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username",mySQLContainer::getUsername);
//        registry.add("spring.datasource.password",mySQLContainer:: getPassword);
//    }

    @Test
    @DisplayName("The MySQL container is created and is running")
    void testContainerIsRunning(){
        assertTrue(mySQLContainer.isCreated(), "MySQL container has not been created.");
        assertTrue(mySQLContainer.isRunning(), "MySQL container is not running.");
    }
}
