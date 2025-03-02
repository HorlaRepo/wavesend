package com.shizzy.moneytransfer.controller;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.model.Beneficiary;
import com.shizzy.moneytransfer.service.UserBeneficiariesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("beneficiaries")
@RequiredArgsConstructor
public class UserBeneficiariesController {

    private final UserBeneficiariesService userBeneficiariesService;

    @PostMapping("/add")
    public ApiResponse<String> addUserBeneficiary(@RequestBody Beneficiary beneficiary, Authentication connectedUser) {
        return userBeneficiariesService.addBeneficiary(connectedUser, beneficiary);
    }

    @GetMapping
    public ApiResponse<List<Beneficiary>> getUserBeneficiaries(Authentication connectedUser) {
        return userBeneficiariesService.getBeneficiaries(connectedUser);
    }

    @GetMapping("/{beneficiaryId}")
    public ApiResponse<Beneficiary> getUserBeneficiary(@PathVariable Long beneficiaryId, Authentication connectedUser) {
        return userBeneficiariesService.getBeneficiary(connectedUser, beneficiaryId);
    }

    @DeleteMapping("/{beneficiaryId}")
    public ApiResponse<String> deleteUserBeneficiary(@PathVariable Long beneficiaryId, Authentication connectedUser) {
        return userBeneficiariesService.deleteBeneficiary(connectedUser, beneficiaryId);
    }
}
