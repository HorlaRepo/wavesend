package com.shizzy.moneytransfer.serviceimpl;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.exception.DuplicateResourceException;
import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
import com.shizzy.moneytransfer.model.Beneficiary;
import com.shizzy.moneytransfer.model.UserBeneficiaries;
import com.shizzy.moneytransfer.repository.UserBeneficiariesRepository;
import com.shizzy.moneytransfer.service.UserBeneficiariesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBeneficiariesServiceImpl implements UserBeneficiariesService {

    private final UserBeneficiariesRepository beneficiariesRepository;

    @Override
    public ApiResponse<String> addBeneficiary(Authentication connectedUser, Beneficiary beneficiary) {

        UserBeneficiaries userBeneficiaries = beneficiariesRepository.findById(connectedUser.getName()).orElseGet(() -> {
            UserBeneficiaries newUserBeneficiaries = new UserBeneficiaries();
            newUserBeneficiaries.setUserId(connectedUser.getName());
            newUserBeneficiaries.setBeneficiaries(new ArrayList<>());
            return newUserBeneficiaries;
        });

        boolean beneficiaryExists = userBeneficiaries.getBeneficiaries().stream()
                .anyMatch(b -> b.getEmail().equals(beneficiary.getEmail()) && b.getName().equals(beneficiary.getName()));

        if (!beneficiaryExists) {
            userBeneficiaries.getBeneficiaries().add(beneficiary);
            beneficiariesRepository.save(userBeneficiaries);
        } else {
            throw new DuplicateResourceException("Beneficiary already exists");
        }

        return ApiResponse.<String>builder()
                .success(true)
                .data("Beneficiary added successfully")
                .message("Beneficiary added successfully")
                .build();
    }

    @Override
    public ApiResponse<String> deleteBeneficiary(Authentication connectedUser, Long beneficiaryId) {
        UserBeneficiaries userBeneficiaries = beneficiariesRepository.findById(connectedUser.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Beneficiary> beneficiaries = userBeneficiaries.getBeneficiaries();
        beneficiaries.removeIf(b -> b.getId().equals(beneficiaryId));
        userBeneficiaries.setBeneficiaries(beneficiaries);
        beneficiariesRepository.save(userBeneficiaries);

        return ApiResponse.<String>builder()
                .success(true)
                .data("Beneficiary deleted successfully")
                .message("Beneficiary deleted successfully")
                .build();
    }

    @Override
    public ApiResponse<Beneficiary> getBeneficiary(Authentication connectedUser, Long beneficiaryId) {
        Beneficiary beneficiary =   beneficiariesRepository.findById(connectedUser.getName())
                .map(UserBeneficiaries::getBeneficiaries)
                .orElse(new ArrayList<>())
                .stream()
                .filter(b -> b.getId().equals(beneficiaryId))
                .findFirst()
                .orElseThrow(()-> new ResourceNotFoundException("Beneficiary not found"));

        return ApiResponse.<Beneficiary>builder()
                .success(true)
                .data(beneficiary)
                .message("Beneficiary retrieved successfully")
                .build();
    }

    @Override
    public ApiResponse<List<Beneficiary>> getBeneficiaries(Authentication connectedUser) {
        List<Beneficiary> beneficiaries =  beneficiariesRepository.findById(connectedUser.getName())
                .map(UserBeneficiaries::getBeneficiaries)
                .orElse(new ArrayList<>());

        return ApiResponse.<List<Beneficiary>>builder()
                .success(true)
                .data(beneficiaries)
                .message("Beneficiaries retrieved successfully")
                .build();
    }
}
