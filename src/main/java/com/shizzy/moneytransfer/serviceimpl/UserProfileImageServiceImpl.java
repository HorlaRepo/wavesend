package com.shizzy.moneytransfer.serviceimpl;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.exception.InvalidFileFormatException;
import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
import com.shizzy.moneytransfer.model.UserProfileImage;
import com.shizzy.moneytransfer.repository.UserProfileImageRepository;
import com.shizzy.moneytransfer.s3.S3Buckets;
import com.shizzy.moneytransfer.s3.S3Service;
import com.shizzy.moneytransfer.service.UserProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import twitter4j.v1.User;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserProfileImageServiceImpl implements UserProfileImageService {

    private final UserProfileImageRepository userProfileImageRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.buckets.wavesend}")
    private String bucketName;

    @Override
    public void updateUserProfileImage(String profileImageUrl, Authentication connectedUser) {
        Optional<UserProfileImage> existingProfileImage = userProfileImageRepository.findByCreatedBy(connectedUser.getName());
        if (existingProfileImage.isPresent()) {
            userProfileImageRepository.updateUserProfileImage(profileImageUrl, connectedUser.getName());
        } else {
            UserProfileImage newUserProfileImage = new UserProfileImage();
            newUserProfileImage.setImageUrl(profileImageUrl);
            newUserProfileImage.setCreatedBy(connectedUser.getName());
            userProfileImageRepository.save(newUserProfileImage);
        }
    }

    @Override
    public byte[] getUserProfileImage(Authentication connectedUser) {

        String userId = connectedUser.getName();

        UserProfileImage userProfileImage = userProfileImageRepository.findByCreatedBy(connectedUser.getName()).orElseThrow(
                () -> new ResourceNotFoundException("User profile image not found"));

        String key = userProfileImage.getImageUrl().replace("https://wavesend.s3.eu-west-1.amazonaws.com/profile-images" + "/" + userId+"/", "");

        return s3Service.getFileFromS3("profile-images/"+userId+"/"+key);
    }

    @Override
    public ApiResponse<String> uploadUserProfileImage(Authentication connectedUser, MultipartFile userProfileImage)  {
        String userId = connectedUser.getName();
        try {
            s3Service.validateProfileImage(userProfileImage);
        } catch (InvalidFileFormatException e) {
            throw new RuntimeException(e);
        }
        String profileImageKey = "profile-images/" + userId + "/" + userProfileImage.getOriginalFilename();
        String profileImageUrl = s3Service.uploadFileToS3(userProfileImage, profileImageKey);

        updateUserProfileImage(profileImageUrl, connectedUser);

        return ApiResponse.<String>builder()
                .success(true)
                .message("User profile image uploaded successfully")
                .data(profileImageUrl)
                .build();

    }

}
