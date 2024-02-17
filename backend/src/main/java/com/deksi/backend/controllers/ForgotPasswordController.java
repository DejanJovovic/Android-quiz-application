package com.deksi.backend.controllers;

import com.deksi.backend.model.User;
import com.deksi.backend.repository.UserRepository;
import com.deksi.backend.service.UserService;
import com.deksi.backend.service.impl.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailSenderService emailService;

    @PostMapping("/change-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not registered");
        }
        // Email exists, generate verification code and send email
        String verificationCode = generateVerificationCode();
        try {
            emailService.sendVerificationCode(email, verificationCode);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send verification code");
        }

        return ResponseEntity.ok("Verification code sent successfully");
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(10000);
        return String.format("%04d", code);
    }

}
