package com.shizzy.moneytransfer.controller;


import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.model.Wallet;
import com.shizzy.moneytransfer.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ApiResponse<Wallet> getWalletByUser(Authentication connectedUser) {
        return  walletService.getWalletByCreatedBy(connectedUser);
    }
}
