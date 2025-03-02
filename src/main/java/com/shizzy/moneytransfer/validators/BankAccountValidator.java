package com.shizzy.moneytransfer.validators;

import com.shizzy.moneytransfer.dto.AddBankAccountRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BankAccountValidator implements ConstraintValidator<ValidBankAccount, AddBankAccountRequest> {

    private static final Set<String> AFRICA_FIELDS = new HashSet<>(Arrays.asList(
            "bankName", "accountNumber", "accountName", "currency", "bankCountry", "bankCode", "paymentMethod"));

    private static final Set<String> EU_FIELDS = new HashSet<>(Arrays.asList(
            "bankName", "accountNumber", "accountType", "currency", "bankCountry", "bankCode", "swiftCode",
            "routingNumber", "beneficiaryName", "beneficiaryAddress", "beneficiaryCountry", "postalCode",
            "streetNumber", "streetName", "city", "paymentMethod"));

    private static final Set<String> US_FIELDS = new HashSet<>(Arrays.asList(
            "bankName", "accountNumber", "accountType", "currency", "bankCountry", "swiftCode",
            "routingNumber", "beneficiaryName", "beneficiaryAddress", "paymentMethod"));

    @Override
    public boolean isValid(AddBankAccountRequest request, ConstraintValidatorContext context) {
        if (request.getRegion() == null || request.getBankAccountDetails() == null) {
            return false;
        }

        Set<String> requiredFields;
        switch (request.getRegion().toLowerCase()) {
            case "africa":
                requiredFields = AFRICA_FIELDS;
                break;
            case "eu":
                requiredFields = EU_FIELDS;
                break;
            case "us":
                requiredFields = US_FIELDS;
                break;
            default:
                return false;
        }

        Map<String, Object> details = request.getBankAccountDetails();

        for (String field : requiredFields) {
            if (!details.containsKey(field) || details.get(field) == null || details.get(field).toString().trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(field + " is required for " + request.getRegion())
                        .addPropertyNode(field)
                        .addConstraintViolation();
                return false;
            }
        }

        if (isNumeric(details.get("accountNumber"))) {
            addViolation(context, "accountNumber must be numeric");
            return false;
        }
        if (details.containsKey("routingNumber") && isNumeric(details.get("routingNumber"))) {
            addViolation(context, "routingNumber must be numeric");
            return false;
        }
        if (details.containsKey("postalCode") && isNumeric(details.get("postalCode"))) {
            addViolation(context, "postalCode must be numeric");
            return false;
        }

        return true;
    }

    private boolean isNumeric(Object value) {
        if (value == null) return true;
        String strValue = value.toString();
        return !strValue.matches("\\d+");
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
