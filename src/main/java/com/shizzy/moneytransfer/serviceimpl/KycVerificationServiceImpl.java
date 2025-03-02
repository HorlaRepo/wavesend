package com.shizzy.moneytransfer.serviceimpl;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.dto.KycVerificationRequest;
import com.shizzy.moneytransfer.enums.VerificationStatus;
import com.shizzy.moneytransfer.exception.InvalidFileFormatException;
import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
import com.shizzy.moneytransfer.model.KycVerification;
import com.shizzy.moneytransfer.repository.KycVerificationRepository;
import com.shizzy.moneytransfer.s3.S3Service;
import com.shizzy.moneytransfer.service.KycVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.keycloak.services.managers.Auth;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.shizzy.moneytransfer.enums.VerificationStatus.*;

@Service
@RequiredArgsConstructor
public class KycVerificationServiceImpl implements KycVerificationService {

    private final KycVerificationRepository kycVerificationRepository;
    private final S3Service s3Service;


    @Override
    public ApiResponse<String> uploadIdDocument(Authentication connectedUser,
                                                @NotNull(value = "Please provide a valid file") MultipartFile idDocument) throws InvalidFileFormatException {
        String userId = connectedUser.getName();

        s3Service.validateKycDocument(idDocument);
        String idKey = "kyc-documents/" + userId + "/id-docs/" + idDocument.getOriginalFilename();

        String idUrl = String.valueOf(s3Service.uploadFileToS3(idDocument, idKey));

        KycVerification kycVerification = kycVerificationRepository.findByUserId(userId)
                .orElse(new KycVerification());
        kycVerification.setUserId(userId);
        kycVerification.setIdDocumentUrl(idUrl);
        kycVerification.setIdVerificationStatus(PENDING);

        kycVerificationRepository.save(kycVerification);

        return ApiResponse.<String>builder()
                .success(true)
                .message("ID document uploaded successfully")
                .data("ID document uploaded successfully")
                .build();
    }

    @Override
    public ApiResponse<String> uploadAddressDocument(Authentication connectedUser,
                                                     @NotNull(value = "Please provide a valid file") MultipartFile addressDocument) {
        String userId = connectedUser.getName();
        Optional<KycVerification> verification = kycVerificationRepository.findByUserId(userId);
        if(verification.isPresent()){
            if(verification.get().getIdVerificationStatus().equals(PENDING)){
                return ApiResponse.<String>builder()
                        .success(false)
                        .message("Please wait for ID verification to be approved before uploading address document")
                        .data("Please wait for ID verification to be approved before uploading address document")
                        .build();
            }
        }
        String addressKey = "kyc-documents/" + userId + "/address-docs/" + addressDocument.getOriginalFilename();
        String addressUrl = s3Service.uploadFileToS3(addressDocument, addressKey);

        KycVerification kycVerification = kycVerificationRepository.findByUserId(userId)
                .orElse(new KycVerification());
        kycVerification.setUserId(userId);
        kycVerification.setAddressDocumentUrl(addressUrl);
        kycVerification.setAddressVerificationStatus(PENDING);

        kycVerificationRepository.save(kycVerification);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Address document uploaded successfully")
                .data("Address document uploaded successfully")
                .build();
    }

    @Override
    public ApiResponse<String> approveAddressVerification(String userId) {
        KycVerification kycVerification = kycVerificationRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("KYC verification data not found"));

        kycVerification.setAddressVerificationStatus(APPROVED);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Address verification approved successfully")
                .data("Address verification approved successfully")
                .build();
    }

    @Override
    public ApiResponse<String> rejectAddressVerification(@NotNull("user id is required") String userId,
                                                         @NotNull("please provide reason for rejection") String rejectionReason) {
        KycVerification kycVerification = kycVerificationRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("KYC verification data not found"));

        kycVerification.setAddressVerificationStatus(REJECTED);
        kycVerification.setAddressRejectionReason(rejectionReason);
        kycVerificationRepository.save(kycVerification);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Address verification rejected successfully")
                .data("Address verification rejected successfully")
                .build();
    }

    @Override
    public ApiResponse<String> approveIdVerification(@NotNull("user id is required") String userId) {
        KycVerification kycVerification = kycVerificationRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("KYC verification request not found"));

        kycVerification.setIdVerificationStatus(APPROVED);

        kycVerificationRepository.save(kycVerification);

        return ApiResponse.<String>builder()
                .success(true)
                .message("ID verification approved successfully")
                .data("ID verification approved successfully")
                .build();
    }

    @Override
    public ApiResponse<String> rejectIdVerification(@NotNull("user id is required") String userId,  @NotNull("please provide reason for rejection") String rejectionReason) {
        KycVerification kycVerification = kycVerificationRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("KYC verification request not found"));

        kycVerification.setIdVerificationStatus(REJECTED);
        kycVerification.setIdRejectionReason(rejectionReason);

        kycVerificationRepository.save(kycVerification);

        return ApiResponse.<String>builder()
                .success(true)
                .message("ID verification rejected successfully")
                .data("ID verification rejected successfully")
                .build();
    }

    @Override
    public ApiResponse<String> updateKyc(Authentication connectedUser, String addressDocumentUrl, String idDocumentUrl) {
        return null;
    }

    @Override
    public ApiResponse<KycVerification> getKycStatus(Authentication connectedUser) {
        KycVerification kycVerification = kycVerificationRepository.findByUserId(connectedUser.getName())
                .orElseThrow(()-> new ResourceNotFoundException("KYC verification data not found"));

        return ApiResponse.<KycVerification>builder()
                .success(true)
                .data(kycVerification)
                .message("KYC verification data found")
                .build();
    }
}
