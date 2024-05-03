package com.deksi.backend.controllers;

import com.deksi.backend.dto.LoginDTO;
import com.deksi.backend.dto.UserDTO;
import com.deksi.backend.dto.UserScoresDTO;
import com.deksi.backend.model.SudokuUserTime;
import com.deksi.backend.model.User;
import com.deksi.backend.model.UserScore;
import com.deksi.backend.repository.SudokuUserTimeRepository;
import com.deksi.backend.repository.UserScoreRepository;
import com.deksi.backend.security.TokenUtils;
import com.deksi.backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<String> updatePassword(HttpServletRequest request, @RequestParam String newPassword) {
        try {
            // Extract token from request headers
            String token = extractTokenFromHeader(request);
            logger.info("Token extracted: {}", token);

            // Validate and decode token
            String username = validateAndExtractUsernameFromToken(token);
            logger.info("Username extracted from token: {}", username);

            // Update password for the authenticated user
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            logger.info("Updating password for user: {}", username);
            userService.updatePassword(username, encodedNewPassword);
            logger.info("Password updated successfully for user: {}", username);

            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            logger.error("Error updating password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating password");
        }
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        // Extract token from Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String validateAndExtractUsernameFromToken(String token) {
        // Extract username from token payload without validating the token
        if (token != null) {
            try {
                // Parse JWT token and extract username using the secret key
                Claims claims = Jwts.parser().setSigningKey(tokenUtils.getTokenSecret()).parseClaimsJws(token).getBody();
                return claims.getSubject();
            } catch (JwtException e) {
                throw new RuntimeException("Failed to parse JWT token", e);
            }
        }
        throw new RuntimeException("Token is missing");
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