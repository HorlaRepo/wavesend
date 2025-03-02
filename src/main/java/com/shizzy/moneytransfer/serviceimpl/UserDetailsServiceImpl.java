//package com.shizzy.moneytransfer.serviceimpl;
//
//import com.shizzy.moneytransfer.model.Admin;
//import com.shizzy.moneytransfer.model.User;
//import com.shizzy.moneytransfer.repository.AdminRepository;
//import com.shizzy.moneytransfer.repository.UserRepository;
//import com.shizzy.moneytransfer.util.CustomUserDetails;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final AdminRepository adminRepository;
//
//    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
//
//        logger.debug("Entering in loadUserByUsername Method...");
//
//        Optional<Admin> adminOptional = adminRepository.findAdminByUsername(username);
//        if (adminOptional.isPresent()) {
//            logger.info("Admin Authenticated Successfully..!!!");
//            return new CustomUserDetails(adminOptional.get(), adminOptional.get().getAdminId());
//        }
//
//        Optional<User> userOptional = userRepository.findUserByEmail(username);
//        if (userOptional.isPresent()) {
//            logger.info("User Authenticated Successfully..!!!");
//            return new CustomUserDetails(userOptional.get(), userOptional.get().getUserId());
//        }
//        throw new UsernameNotFoundException("User not found with username: " + username);
//    }
//}
