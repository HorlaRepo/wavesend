//package com.shizzy.moneytransfer.service;
//
//import com.shizzy.moneytransfer.api.ApiResponse;
//import com.shizzy.moneytransfer.exception.DuplicateResourceException;
//import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
//import com.shizzy.moneytransfer.model.Admin;
//import com.shizzy.moneytransfer.model.UserRole;
//import com.shizzy.moneytransfer.repository.AdminRepository;
//import com.shizzy.moneytransfer.repository.UserRoleRepository;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validator;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
//
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class AdminServiceTest {
//    private AdminService adminService;
//    private AutoCloseable autoCloseable;
//    private Validator validator;
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private AdminRepository adminRepository;
//    @Mock
//    UserRoleRepository roleRepository;
//
//    @BeforeEach
//    void setUp() {
//        passwordEncoder = new BCryptPasswordEncoder();
//        autoCloseable = MockitoAnnotations.openMocks(this);
//        adminService = new AdminService(adminRepository, passwordEncoder, roleRepository);
//        validator = new LocalValidatorFactoryBean();
//        ((LocalValidatorFactoryBean) validator).afterPropertiesSet();
//    }
//
//    @AfterEach
//    void tearDown() throws Exception{
//        autoCloseable.close();
//    }
//
//    private UserRole getRole(){
//        String userRoleName = "ADMIN";
//
//        // Mock the behavior of UserRoleRepository to return a UserRole when save is called
//        UserRole userRole = new UserRole();
//        userRole.setName(userRoleName);
//
//        roleRepository.save(userRole);
//
//        when(roleRepository.findByName(userRoleName)).thenReturn(Optional.of(userRole));
//
//        return  userRole;
//    }
//
//    @Test
//    void canAddAdmin() {
//        // Given
//        String adminUsername = "admin";
//        String adminEmail = "admin@yahoo.com";
//        String adminPassword = passwordEncoder.encode("adminPass");
//
//        Admin admin  = Admin.builder()
//                .username(adminUsername)
//                .email(adminEmail)
//                .password(adminPassword)
//                .roles(Set.of(getRole()))
//                        .build();
//
//        // When
//        adminService.addAdmin(admin);
//
//        // Then
//        verify(roleRepository).save(any(UserRole.class)); // Ensure UserRole is saved
//        verify(adminRepository).save(any(Admin.class)); // Ensure Admin is saved
//    }
//
//    @Test
//    public void willThrowExceptionIfUsernameExists() {
//        // Given
//        String existingUsername = "shizzy";
//        Admin admin = Admin.builder()
//                .username(existingUsername)
//                .password(passwordEncoder.encode("shizzy0"))
//                .email("shizzy@mail.com")
//                .build();
//
//        // Mock the AdminRepository to return true when existsByUsername is called with the given username
//        when(adminRepository.existsAdminByUsername(existingUsername)).thenReturn(true);
//
//        // Then
//        assertThatThrownBy(() -> adminService.addAdmin(admin))
//                .isInstanceOf(DuplicateResourceException.class)
//                .hasMessage("Username already taken");
//    }
//
//    @Test
//    public void willThrowExceptionIfEmailExists() {
//
//        // Given
//        Admin existingAdmin = Admin.builder()
//                .username("shizzy")
//                .password("shizyoo")
//                .email("shizzy@yahoo.com")
//                .build();
//
//        // Mock the AdminRepository to return true when existsByUsername is called with the given username
//        when(adminRepository.existsAdminByEmail(existingAdmin.getEmail())).thenReturn(true);
//
//        // Then
//        assertThatThrownBy(() -> adminService.addAdmin(existingAdmin))
//                .isInstanceOf(DuplicateResourceException.class)
//                .hasMessage("Email already taken");
//    }
//
//
//    @Test
//    public void getAllAdminsWhenAdminsExist() {
//        // Given
//        List<Admin> admins = new ArrayList<>();
//        admins.add(new Admin()); // Add some sample admins
//        admins.add(new Admin());
//
//        // Mock the behavior of adminRepository.findAll() to return the list of admins
//        when(adminRepository.findAll()).thenReturn(admins);
//
//        // When
//        ApiResponse<List<Admin>> response = adminService.getAllAdmins();
//
//        // Then
//        assertTrue(response.isSuccess());
//        assertEquals(admins.size()+" Admins found", response.getMessage());
//        assertEquals(admins, response.getData());
//    }
//
//    @Test
//    public void willThrowExceptionWhenNoAdminsExist() {
//        // Given
//        List<Admin> emptyList = new ArrayList<>();
//
//        // Mock the behavior of adminRepository.findAll() to return an empty list
//        when(adminRepository.findAll()).thenReturn(emptyList);
//
//        // Then
//        assertThatThrownBy(() -> adminService.getAllAdmins())
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessage("No admin found");
//    }
//
//    @Test
//    void willThrowExceptionWhenEmailIsInvalid() {
//        // Given
//        Admin admin = Admin.builder()
//                .email("shizzy.mail.ca")
//                .password("Mymail22@@@")
//                .username("shizzy0")
//                .build();
//
//        // When
//        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
//
//        // Then
//        assertEquals(1, violations.size());
//        ConstraintViolation<Admin> violation = violations.iterator().next();
//        assertEquals("Please provide a valid email address", violation.getMessage());
//        assertEquals("email", violation.getPropertyPath().toString());
//    }
//
//    @Test
//    public void willThrowExceptionWhenEmailIsBlank() {
//        // Given
//        Admin admin = Admin.builder()
//                .email("")
//                .password("Mymail22@!")
//                .username("shizzy0")
//                .build();
//
//        // When
//        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
//
//        // Then
//        assertEquals(1, violations.size());
//        ConstraintViolation<Admin> violation = violations.iterator().next();
//        assertEquals("Email field cannot be blank", violation.getMessage());
//        assertEquals("email", violation.getPropertyPath().toString());
//    }
//
//    @Test
//    public void testValidPassword() {
//        Admin admin = Admin.builder()
//                .username("shizzy")
//                .password("Password@123")
//                .email("shiz@mail.com")
//                .build();
//
//        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
//        assertTrue(violations.isEmpty(), "Valid password should not have any violations");
//    }
//
//    @Test
//    public void willThrowWhenPasswordIsInvalid() {
//        Admin admin = Admin.builder()
//                .username("shizzy")
//                .password("password123")
//                .email("shiz@mail.com")
//                .build();
//
//        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
//        assertFalse(violations.isEmpty(), "Invalid password should have violations");
//        assertEquals("Password must contain at least one uppercase, one lowercase, one number, and one special character.", violations.iterator().next().getMessage());
//    }
//
//    @Test
//    public void willThrowExceptionWhenUsernameIsBlank() {
//        // Create a User object with a blank first name
//        Admin admin = new Admin();
//        admin.setUsername("");
//
//        // Validate the first username field using the validator
//        Set<ConstraintViolation<Admin>> violations = validator.validateProperty(admin, "username");
//
//        // Ensure that there is at least one violation, indicating that the username is blank
//        assertThat(violations).isNotEmpty();
//
//        // Check the violation message
//        String expectedMessage = "Username field cannot be blank";
//        assertThat(violations.iterator().next().getMessage()).isEqualTo(expectedMessage);
//    }
//
//
//}