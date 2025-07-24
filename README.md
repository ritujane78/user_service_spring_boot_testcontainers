# Spring Boot Web layer and Integration Tests (/test/java/com/jane/tutorials/junit/ui.controllers)

Test Methods

Web Layer tests:

testCreateUser_whenValidUserDetailsProvided_returnsCreatedUserDetails
testCreateUser_whenFirstNameIsNotProvided_returns400StatusCode
testCreateUser_whenFirstNameIsOnlyOneCharacter_returns400StatusCode

Integration tests

testCreateUser_whenValidUserDetailsProvided_returnCreatedUserDetails
When a user is successfully logged in , a JWT token is returned in the header of the JSON and all the test methods are executed in order. To verify this: testUserLogin_whenValidCredentialsProvided_returnsJWTInAuthorizationHeader

To test if the logged in user is returned when a JWT token is passed and the instance of the test class is configured to be created only once: testGetUsers_whenValidJWTTokenProvided_returnsUsers

# Tests for Entities (/test/java/com/jane/tutorials/junit/io)

@DataJpaTest annotation is used for testing persistence.
Changes are not made in the production database but in-memory.

Integration tests for UserEntity

testUserEntity_whenValidUserDetailsProvided_shouldReturnStoredUserDetails
testUserEntity_whenExistingUserIdProvided_shouldThrowException
testUserEntity_whenFirstNameIsTooLong_shouldThrowException

Integration tests for UsersRepository methods

testUsersRepository_whenValidEmailProvided_returnsUserWithThatEmail
testUsersRepository_whenValidUserIdProvided_returnsUserWithThatUserId
testUsersRepository_whenGivenEmailDomain_returnsUsersWithGivenEmailDomain

# Tests in a  Docker testcontainer (/test/java/com/jane/tutorials/junit/ui.controllers)

Annotations: @Testcontainers,  @ServiceConnection,
    @Container
or
    static {
        mySQLContainer.start();
    }
Basic Test Methods

testContainerIsRunning
testCreateUser_whenValidDetailsProvided_returnsUserDetails
testGetUsers_whenMissingJWT_returns403
testUserLogin_whenValidCredentialsProvided_returnsJWTinAuthorizationHeader
testGetUsers_whenValidJWTProvided_returnsUsers