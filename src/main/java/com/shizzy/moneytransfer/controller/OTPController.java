package com.shizzy.moneytransfer.controller;

import com.shizzy.moneytransfer.serviceimpl.TwilioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("otp")
public class OTPController {

    private final TwilioServiceImpl twilioServiceImpl;

    @PostMapping("/send")
    public ResponseEntity<String> sendOTP(@RequestParam String phoneNumber) {
        twilioServiceImpl.sendOTP(phoneNumber);
        return ResponseEntity.ok("OTP sent successfully to " + phoneNumber);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOTP(@RequestParam String phoneNumber, @RequestParam String otp) {
        boolean isValid = twilioServiceImpl.verifyOTP(phoneNumber, otp);
        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.status(400).body("Invalid OTP.");
        }
    }

    @PostMapping("/verif")
    public ResponseEntity<String> verify() {
        twilioServiceImpl.verify();
        return ResponseEntity.ok("OTP verified successfully.");
    }
}
