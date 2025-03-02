//package com.shizzy.moneytransfer.serviceimpl;
//
//import com.shizzy.moneytransfer.api.ApiResponse;
//import com.shizzy.moneytransfer.dto.AdminRegistrationRequestBody;
//import com.shizzy.moneytransfer.dto.AuthRequestDTO;
//import com.shizzy.moneytransfer.dto.JwtResponseDTO;
//import com.shizzy.moneytransfer.dto.UserRegistrationRequestBody;
//import com.shizzy.moneytransfer.enums.EmailTemplateName;
//import com.shizzy.moneytransfer.exception.DuplicateResourceException;
//import com.shizzy.moneytransfer.exception.IllegalArgumentException;
//import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
//import com.shizzy.moneytransfer.helpers.ValidatableEntity;
//import com.shizzy.moneytransfer.model.Admin;
//import com.shizzy.moneytransfer.model.Token;
//import com.shizzy.moneytransfer.model.User;
//import com.shizzy.moneytransfer.model.UserRole;
//import com.shizzy.moneytransfer.repository.AdminRepository;
//import com.shizzy.moneytransfer.repository.TokenRepository;
//import com.shizzy.moneytransfer.repository.UserRepository;
//import com.shizzy.moneytransfer.repository.UserRoleRepository;
//import com.shizzy.moneytransfer.security.jwt.JwtService;
//import com.shizzy.moneytransfer.service.AuthService;
//import com.shizzy.moneytransfer.service.EmailService;
//import com.shizzy.moneytransfer.service.WalletService;
//import com.shizzy.moneytransfer.util.CustomUserDetails;
//import jakarta.mail.MessagingException;
//import jakarta.transaction.Transactional;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.security.SecureRandom;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
////@Service
//@RequiredArgsConstructor
//public class AuthServiceImpl implements AuthService {
//    private final UserRepository userRepository;
//    private final WalletService walletService;
//    private final PasswordEncoder passwordEncoder;
//    private final TokenRepository tokenRepository;
//    private final AdminRepository adminRepository;
//    private final UserRoleRepository roleRepository;
//    private final EmailService emailService;
//
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//
//    @Value("${application.mailing.frontend.activation-url}")
//    private String activationUrl;
//
//    @Value("${application.mailing.frontend.password-reset-url}")
//    private String passwordResetUrl;
//
//
//    @Override
//    public ApiResponse<User> registerUser(@NonNull UserRegistrationRequestBody requestBody) throws MessagingException {
//
//        if(userRepository.existsUserByEmail(requestBody.getEmail())) {
//            throw new DuplicateResourceException("User with this email already exist");
//        }
//
//        User newUser = User.builder()
//                .firstName(requestBody.getFirstName())
//                .lastName(requestBody.getLastName())
//                .email(requestBody.getEmail())
//                .password(passwordEncoder.encode(requestBody.getPassword()))
//                .phoneNumber(requestBody.getPhoneNumber())
//                .gender(requestBody.getGender())
//                .address(requestBody.getAddress())
//                .dateOfBirth(LocalDate.parse(requestBody.getDateOfBirth()))
//                .accountLocked(false)
//                .enabled(false)
//                .wallet(walletService.createWallet())
//                .build();
//
//        userRepository.save(newUser);
//
//        var token = generateAndSaveToken(newUser);
//
//        Map<String, Object> properties = Map.of(
//                "username", newUser.getFullName(),
//                "confirmationUrl", activationUrl,
//                "activationCode", token
//        );
//
//        //TODO: Send email to user to verify email address
//        sendValidationEmail(newUser, properties, EmailTemplateName.ACTIVATE_ACCOUNT, "Activate your account");
//
//        return new ApiResponse<>(true, "User added successfully", newUser);
//    }
//
//    @Transactional
//    @Override
//    public ApiResponse<String> registerAdmin(AdminRegistrationRequestBody requestBody) throws MessagingException {
//
//        if(adminRepository.existsAdminByUsername(requestBody.getUsername())) {
//            throw new DuplicateResourceException("Username already taken");
//        }
//
//        if(adminRepository.existsAdminByEmail(requestBody.getEmail())) {
//            throw new DuplicateResourceException("Email already taken");
//        }
//
//        Admin newAdmin = Admin.builder()
//                .username(requestBody.getUsername())
//                .firstName(requestBody.getFirstName())
//                .lastName(requestBody.getLastName())
//                .dateOfBirth(LocalDate.parse(requestBody.getDateOfBirth()))
//                .password(passwordEncoder.encode(requestBody.getPassword()))
//                .email(requestBody.getEmail())
//                .roles(getRole(requestBody.getRoles()))
//                .accountLocked(false)
//                .enabled(false)
//                .build();
//
//        adminRepository.save(newAdmin);
//
//        var token = generateAndSaveToken(newAdmin);
//
//        Map<String, Object> properties = Map.of(
//                "username", newAdmin.getFullName(),
//                "confirmationUrl", activationUrl,
//                "activationCode", token
//        );
//        sendValidationEmail(newAdmin, properties, EmailTemplateName.ACTIVATE_ACCOUNT, "Activate your account");
//
//        return new ApiResponse<>(true, "New admin created", newAdmin.getUsername());
//    }
//
//
//    @Override
//    public ApiResponse<JwtResponseDTO> authenticateAndGetToken(AuthRequestDTO authRequestDTO){
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
//        if(authentication.isAuthenticated()) {
//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//            final JwtResponseDTO responseDTO = JwtResponseDTO.builder()
//                    .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername(), userDetails.getId()))
//                    .username(authRequestDTO.getUsername())
//                    .user(userRepository.findUserByEmail(authRequestDTO.getUsername()).orElse(null))
//                    .build();
//            return ApiResponse.<JwtResponseDTO>builder()
//                    .success(true)
//                    .message("User logged in successfully")
//                    .data(responseDTO)
//                    .build();
//        } else {
//            throw new UsernameNotFoundException("user does not exist..!!");
//        }
//    }
//
//    @Override
//    public ApiResponse<String> activateAccount(String token) throws MessagingException {
//
//        Token activationToken = tokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Invalid token"));
//        if(activationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
//            var newToken = generateAndSaveToken(activationToken.getUser() != null ? activationToken.getUser() : activationToken.getAdmin());
//            Map<String, Object> properties = Map.of(
//                    "username", activationToken.getUser() != null ? activationToken.getUser().getFullName() : activationToken.getAdmin().getFullName(),
//                    "confirmationUrl", activationUrl,
//                    "activationCode", token
//            );
//            sendValidationEmail(
//                    activationToken.getUser() != null ? activationToken.getUser() :
//                            activationToken.getAdmin(), properties, EmailTemplateName.ACTIVATE_ACCOUNT,
//                    "Activate your account"
//            );
//            tokenRepository.delete(activationToken);
//            //Todo: Custom exception
//            throw new IllegalArgumentException("Token has expired. A new token has been sent to your email address.");
//        }
//
//        if(activationToken.getUser() != null) {
//            User user = activationToken.getUser();
//            user.setEnabled(true);
//            userRepository.save(user);
//        }
//
//        if(activationToken.getAdmin() != null) {
//            Admin admin = activationToken.getAdmin();
//            admin.setEnabled(true);
//            adminRepository.save(admin);
//        }
//
//        activationToken.setValidatedAt(LocalDateTime.now());
//        tokenRepository.save(activationToken);
//        tokenRepository.delete(activationToken);
//
//        return ApiResponse.<String>builder()
//                .success(true)
//                .message("Account activated successfully")
//                .data("Account activated successfully")
//                .build();
//    }
//
//    @Override
//    public ApiResponse<String> requestPasswordReset(String email) throws MessagingException {
//
//        User user = userRepository.findUserByEmail(email).orElseThrow(
//                ()-> new ResourceNotFoundException("User with this email does not exist"));
//        var token = generateAndSaveToken(user);
//
//        Map<String, Object> properties = Map.of(
//                "username", user.getFullName(),
//                "passwordResetUrl", passwordResetUrl,
//                "activationCode", token
//        );
//
//        sendValidationEmail(user, properties, EmailTemplateName.PASSWORD_RESET, "Verify password reset request");
//
//        return ApiResponse.<String>builder()
//                .success(true)
//                .message("Password reset link sent to your email address")
//                .data("Password reset link sent to your email address")
//                .build();
//    }
//
//    @Override
//    public ApiResponse<String> resetPassword(String token, String newPassword) {
//        return null;
//    }
//
//    private void sendValidationEmail(ValidatableEntity validatableEntity,
//                                     Map<String, Object> properties,
//                                     EmailTemplateName templateName,
//                                     String subject) throws MessagingException {
//        var token = generateAndSaveToken(validatableEntity);
////        Map<String, Object> properties = Map.of(
////                "username", validatableEntity.getFullName(),
////                "confirmationUrl", activationUrl,
////                "activationCode", token
////        );
//        emailService.sendEmail(
//                validatableEntity.getEmail(),
//                properties,
//                templateName,
//                subject
//        );
//    }
//
//    private String generateAndSaveToken(ValidatableEntity validatableEntity) {
//        String generatedToken = generateActivationCode(6);
//        Token.TokenBuilder tokenBuilder = Token.builder()
//                .token(generatedToken)
//                .createdAt(LocalDateTime.now())
//                .expiresAt(LocalDateTime.now().plusMinutes(15));
//        if (validatableEntity.user() == null) {
//            tokenBuilder.admin(validatableEntity.admin());
//        }
//
//        if (validatableEntity.admin() == null) {
//            tokenBuilder.user(validatableEntity.user());
//        }
//
//        Token token = tokenBuilder.build();
//
//        tokenRepository.save(token);
//
//        return generatedToken;
//    }
//
//
//    private String generateActivationCode(int length) {
//        String characters = "0123456789";
//        StringBuilder builder = new StringBuilder();
//        SecureRandom random = new SecureRandom();
//        for(int i = 0; i < length; i++) {
//            builder.append(characters.charAt(random.nextInt(characters.length())));
//        }
//        return builder.toString();
//    }
//
//
//    private Set<UserRole> getRole (Set<UserRole> newRole){
//        if(newRole.isEmpty()){
//            throw new IllegalArgumentException("Admin role cannot be empty");
//        }
//        String name = "";
//        Set<UserRole> selectedRole = new HashSet<>();
//        for(UserRole role : newRole) {
//            name = role.getName();
//            UserRole userRole = roleRepository.findByName(name).orElseThrow(()-> new ResourceNotFoundException("Selected role does not exist"));
//            selectedRole.add(userRole);
//        }
//        return selectedRole;
//    }
//}
