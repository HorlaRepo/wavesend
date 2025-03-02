package com.shizzy.moneytransfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account"),
    DEPOSIT_SUCCESS("deposit_success"),
    PASSWORD_RESET("password_reset");

    private final String name;
}
