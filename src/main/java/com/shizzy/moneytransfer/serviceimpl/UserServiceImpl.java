//package com.shizzy.moneytransfer.serviceimpl;
//
//import com.shizzy.moneytransfer.api.ApiResponse;
//import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
//import com.shizzy.moneytransfer.model.User;
//import com.shizzy.moneytransfer.repository.UserRepository;
//import com.shizzy.moneytransfer.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@RequiredArgsConstructor
////@Service
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//
//    @Cacheable("users")
//    @Override
//    public ApiResponse<List<User>> getAllUsers(Integer page, Integer pageSize) {
//        PageRequest pageRequest = PageRequest.of(page, pageSize);
//        List<User> users = userRepository.findAll(pageRequest).getContent();
//        if(users.isEmpty())
//            throw new ResourceNotFoundException("No users found");
//        return new ApiResponse<>(true, users.size()+" Users found", users);
//    }
//
//    @Override
//    public ApiResponse<User> getUserById(Integer id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(()-> new ResourceNotFoundException("User does not exist"));
//
//        return new ApiResponse<>(true, "User exists", user);
//    }
//
//    @Override
//    public ApiResponse<User> getUserByEmail(String email) {
//        User user = userRepository.findUserByEmail(email)
//                .orElseThrow(()-> new ResourceNotFoundException("User does not exist"));
//
//        return new ApiResponse<>(true, "User exists", user);
//    }
//
//    @Override
//    public void deleteUserById(Integer id) {
//        checkIfUserExistsOrThrow(id);
//        userRepository.deleteById(id);
//        new ApiResponse<>(true, "User deleted successfully", id);
//    }
//
//    @Override
//    public void checkIfUserExistsOrThrow(Integer id) {
//        if(!userRepository.existsById(id)){
//            throw new ResourceNotFoundException(
//                    "User with id [%s] not found ".formatted(id));
//        }
//    }
//
//    @Override
//    public User findUserOrThrow(@NotNull String email) {
//        Optional<User> existingUser = userRepository.findUserByEmail(email);
//        return existingUser.orElseThrow(()-> new ResourceNotFoundException("User with email: "+email+" not found"));
//    }
//
//
//}
