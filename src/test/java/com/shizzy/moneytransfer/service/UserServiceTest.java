//package com.shizzy.moneytransfer.service;
//
//import com.shizzy.moneytransfer.exception.DuplicateResourceException;
//import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
//import com.shizzy.moneytransfer.model.User;
//import com.shizzy.moneytransfer.repository.UserRepository;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validator;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class UserServiceTest {
//    private UserService userService;
//    private AutoCloseable autoCloseable;
//    private Validator validator;
//
//    @Mock
//    private UserRepository userRepository;
//
//
//    @BeforeEach
//    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
//        userService = new UserService(userRepository);
//        validator = new LocalValidatorFactoryBean();
//        ((LocalValidatorFactoryBean) validator).afterPropertiesSet();
//    }
//
//    @AfterEach
//    void tearDown() throws Exception{
//        autoCloseable.close();
//    }
//
//    @Test
//    void getAllUsers() {
//        Page<User> page = mock(Page.class);
//        List<User> userList = List.of(new User());
//        when(page.getContent()).thenReturn(userList);
//        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
//
//        //When
//        List<User> expected =  userService.getAllUsers(0,200).getData();
//
//        //Then
//        assertThat(expected).isEqualTo(userList);
//        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
//        verify(userRepository).findAll(pageableArgumentCaptor.capture());
//        assertThat(pageableArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(200));
//
//    }
//
//    @Test
//    public void willThrowExceptionWhenUsersListIsEmpty() {
//        // Create an empty page of users
//        Page<User> emptyUserPage = new PageImpl<>(Collections.emptyList());
//
//        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
//        // Mock the userRepository to return the empty page of users
//        when(userRepository.findAll(pageableCaptor.capture())).thenReturn(emptyUserPage);
//
//        // Test if ResourceNotFoundException is thrown when the page of users is empty
//        assertThatThrownBy(() -> userService.getAllUsers(0, 10))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessage("No users found");
//
//        // Verify that the findAll method was called with the correct Pageable arguments
//        Pageable pageableUsed = pageableCaptor.getValue();
//        assertThat(pageableUsed.getPageNumber()).isEqualTo(0);
//        assertThat(pageableUsed.getPageSize()).isEqualTo(10);
//    }
//
//    @Test
//    void canGetUserById() {
//
//        //Given
//        Integer userId  = 1;
//        User user = createUser();
//        user.setUserId(userId);
//
//        //When
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        //Then
//        Optional<User> result = Optional.ofNullable(userService.getUserById(userId).getData());
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals(user, result.get());
//    }
//
//    private User createUser() {
//        return
//                User.builder()
//                .firstName("Fred")
//                .lastName("James")
//                .email("fjames@gmail.com")
//                .build();
//    }
//
//    @Test
//    void canAddUser() {
//
//        User user = createUser();
//
//        userService.addUser(user);
//
//        verify(userRepository).save(user);
//
//    }
//
//    @Test
//    void willThrowExceptionWhenUserExists() {
//        // Mock the userRepository to return true when existsByEmail is called with the given email
//        String existingEmail = "existing@example.com";
//        User user = createUser();
//        user.setEmail(existingEmail);
//
//        when(userRepository.existsUserByEmail(existingEmail)).thenReturn(true);
//
//        // Test if DuplicateResourceException is thrown when the user with the same email already exists
//        assertThatThrownBy(() -> userService.addUser(user))
//                .isInstanceOf(DuplicateResourceException.class)
//                .hasMessage("User with this email already exist");
//    }
//
//    @Test
//    void willThrowExceptionWhenEmailIsInvalid() {
//        User user = new User();
//        user.setEmail("jboss.mail.com"); // Provide an invalid email address
//
//        // Validate the email field using the validator
//        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "email");
//
//        // Ensure that there is at least one violation, indicating that the email is invalid
//        assertThat(violations).isNotEmpty();
//
//        // Check the violation message
//        String expectedMessage = "Please provide a valid email address";
//        assertThat(violations.iterator().next().getMessage()).isEqualTo(expectedMessage);
//    }
//
//    @Test
//    public void willThrowExceptionWhenFirstNameIsBlank() {
//        // Create a User object with a blank first name
//        User user = new User();
//        user.setFirstName("");
//
//        // Validate the first name field using the validator
//        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "firstName");
//
//        // Ensure that there is at least one violation, indicating that the first name is blank
//        assertThat(violations).isNotEmpty();
//
//        // Check the violation message
//        String expectedMessage = "First name cannot be blank";
//        assertThat(violations.iterator().next().getMessage()).isEqualTo(expectedMessage);
//    }
//
//    @Test
//    public void willThrowExceptionWhenLastNameIsBlank() {
//        // Create a User object with a blank last name
//        User user = new User();
//        user.setLastName(" ");
//
//        // Validate the last name field using the validator
//        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "lastName");
//
//        // Ensure that there is at least one violation, indicating that the last name is blank
//        assertThat(violations).isNotEmpty();
//
//        // Check the violation message
//        String expectedMessage = "Last name cannot be blank";
//        assertThat(violations.iterator().next().getMessage()).isEqualTo(expectedMessage);
//    }
//
//    @Test
//    void deleteUserById() {
//        Integer userId = 1;
//
//        // Mock the UserRepository to return true when existsById is called with the given user ID
//        when(userRepository.existsById(userId)).thenReturn(true);
//
//        // When
//        userService.deleteUserById(userId);
//
//        // Then
//        verify(userRepository).deleteById(userId);
//    }
//
//    @Test
//    public void checkIfUserExistsOrThrowResourceNotFoundException() {
//        // Given
//        Integer userId = 123;
//
//        // Mock the UserRepository to return false when existsById is called with the given user ID
//        when(userRepository.existsById(userId)).thenReturn(false);
//
//        // Then
//        assertThatThrownBy(() -> userService.checkIfUserExistsOrThrow(userId))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessage("User with id [%s] not found ".formatted(userId));
//    }
//}