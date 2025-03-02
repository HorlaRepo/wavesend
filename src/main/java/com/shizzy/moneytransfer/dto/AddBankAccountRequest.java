package com.shizzy.moneytransfer.dto;

import com.shizzy.moneytransfer.validators.ValidBankAccount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ValidBankAccount
public class AddBankAccountRequest {

    @NotBlank(message = "Region  is required")
    @NotNull(message = "Region is required")
    private String region;

    private Map<String, Object> bankAccountDetails;

//    @NotBlank(message = "Bank Name is required")
//    @NotNull(message = "Bank Name is required")
//    private String bankName;
//
//    @NotBlank(message = "Account Number is required")
//    @NotNull(message = "Account Number is required")
//    private String accountNumber;
//
//    @NotBlank(message = "Account Name is required")
//    @NotNull(message = "Account Name is required")
//    private String accountName;
//
//    @NotBlank(message = "Account Type is required")
//    @NotNull(message = "Account Type is required")
//    private String accountType;
//
//    @NotBlank(message = "Bank Country is required")
//    @NotNull(message = "Bank Country is required")
//    private String bankCountry;
//
//    @NotBlank(message = "Bank Code is required")
//    @NotNull(message = "Bank Code is required")
//    private String bankCode;
}
