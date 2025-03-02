package com.shizzy.moneytransfer.service;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.model.Beneficiary;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserBeneficiariesService {
    ApiResponse<String> addBeneficiary(Authentication connectedUser, Beneficiary beneficiary);
    ApiResponse<String> deleteBeneficiary(Authentication connectedUser, Long beneficiaryId);
    ApiResponse<Beneficiary> getBeneficiary(Authentication connectedUser, Long beneficiaryId);
    ApiResponse<List<Beneficiary>> getBeneficiaries(Authentication connectedUser);
}
