package com.shizzy.moneytransfer.service;

import com.shizzy.moneytransfer.api.ApiResponse;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakService {
    ApiResponse<UserRepresentation> existsUserByEmail(String email);
    ApiResponse<UserRepresentation> getUserById(String userId);
}
