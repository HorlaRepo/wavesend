package com.shizzy.moneytransfer.controller;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.service.KeycloakService;
import com.shizzy.moneytransfer.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keycloak")
public class KeycloakEventsController {

    private final WalletService walletService;

    @Value("${keycloak.event-listener.endpoint-secret}")
    private String endpointSecret;

    private final KeycloakService keycloakService;
    private static final Logger log = LoggerFactory.getLogger(KeycloakEventsController.class);

    @PostMapping("/events")
    public ResponseEntity<String> receiveEvent(@RequestBody String event, @RequestHeader("Sig-Header") String sigHeader){
        if (!endpointSecret.equals(sigHeader)) {
            log.error("Invalid secret key");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid secret key");
        }
        log.info("Received event: {}", event);
        walletService.createWallet(event);
        return ResponseEntity.ok("Event received");
    }

    @GetMapping("/check-user")
    public ApiResponse<UserRepresentation> checkUser(@RequestParam String email) {
        return keycloakService.existsUserByEmail(email);
    }
}
