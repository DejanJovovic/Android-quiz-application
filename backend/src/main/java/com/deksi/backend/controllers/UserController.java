package com.deksi.backend.controllers;

import com.deksi.backend.dto.LoginDTO;
import com.deksi.backend.dto.UserDTO;
import com.deksi.backend.dto.UserScoresDTO;
import com.deksi.backend.model.SudokuUserTime;
import com.deksi.backend.model.User;
import com.deksi.backend.model.UserScore;
import com.deksi.backend.model.VerificationCode;
import com.deksi.backend.repository.SudokuUserTimeRepository;
import com.deksi.backend.repository.UserScoreRepository;
import com.deksi.backend.security.TokenUtils;
import com.deksi.backend.service.UserService;
import com.deksi.backend.service.impl.UserNotFoundException;
import com.deksi.backend.service.impl.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserScoreRepository userScoreRepository;

    @Autowired
    private SudokuUserTimeRepository sudokuUserTimeRepository;

    @Autowired
    private VerificationCodeService verificationCodeService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.findByUsername(userDTO.getUsername());
        if (user != null) {
            return ResponseEntity.badRequest().body("Username or email taken!");
        }
            String password = passwordEncoder.encode(userDTO.getPassword());
        User userNew = new User(userDTO.getEmail(), userDTO.getUsername(), password);
            return ResponseEntity.ok(userService.signUp(userNew));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error signing up user");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername());
            String userEmail = userService.findByUsername(loginDTO.getUsername()).getEmail();

            return ResponseEntity.ok(Map.of("token", tokenUtils.generateToken(userDetails), "email", userEmail));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String email,
                                                 @RequestParam String newPassword,
                                                 @RequestParam String verificationCode) {
        try {
            // Log request details
            logger.info("Received request to update password for email: {}", email);

            // Retrieve verification code from the database
            VerificationCode savedVerificationCode = verificationCodeService.getVerificationCodeByEmail(email);

            // Check if the saved verification code matches the one provided
            if (savedVerificationCode == null || !savedVerificationCode.getCode().equals(verificationCode)) {
                // If verification code is invalid, return a bad request response
                logger.error("Invalid verification code for email: {}", email);
                return ResponseEntity.badRequest().body("Invalid verification code");
            }

            String encodedNewPassword = passwordEncoder.encode(newPassword);
            userService.updatePassword(email, encodedNewPassword, verificationCode);

            // If no exceptions are thrown, the password was successfully updated
            logger.info("Password changed to: {}", newPassword);
            return ResponseEntity.ok("Password updated successfully");
        } catch (UserNotFoundException e) {
            // Handle case when user with given email is not found
            logger.error("User not found for email: {}", email, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            // Handle case when verification code is invalid
            logger.error("Invalid verification code for email: {}", email, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            logger.error("Error updating password for email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating password");
        }
    }

    @PostMapping("/update-score")
    public ResponseEntity<String> updateScore(@RequestBody UserScore userScore) {
        // Check if the user exists in the database
        UserScore existingUserScore = userScoreRepository.findByUsername(userScore.getUsername());

        if (existingUserScore != null) {
            // Update the user's totalScore
            existingUserScore.setTotalPoints(userScore.getTotalPoints());
            userScoreRepository.save(existingUserScore);
            return ResponseEntity.ok("Score updated successfully");
        } else {
            // Create a new record for the user
            userScoreRepository.save(userScore);
            return ResponseEntity.ok("New user score created successfully");
        }
    }

    @GetMapping("/user-scores")
    public List<UserScore> getUserScores() {
        return userScoreRepository.findAll();
    }

    @PostMapping("/sudoku-update-time")
    public ResponseEntity<String> updateTime(@RequestBody SudokuUserTime sudokuUserTime) {
        // Check if the user exists in the database
        SudokuUserTime existingUserTime = sudokuUserTimeRepository.findByUsername(sudokuUserTime.getUsername());

        if (existingUserTime != null) {
            existingUserTime.setTotalTime(sudokuUserTime.getTotalTime());
            existingUserTime.setDifficulty(sudokuUserTime.getDifficulty());
            sudokuUserTimeRepository.save(existingUserTime);
            return ResponseEntity.ok("Time updated successfully");
        } else {
            // Create a new record for the user
            sudokuUserTimeRepository.save(sudokuUserTime);
            return ResponseEntity.ok("New user time created successfully");
        }
    }

    @GetMapping("/sudoku-user-time")
    public List<SudokuUserTime> getSudokuUserTime() {
        return sudokuUserTimeRepository.findAll();
    }
}