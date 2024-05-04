package com.deksi.backend.service.impl;

import com.deksi.backend.model.User;
import com.deksi.backend.repository.UserRepository;
import com.deksi.backend.repository.VerificationCodeRepository;
import com.deksi.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Primary
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    public UserServiceImpl(UserRepository userRepository, VerificationCodeService verificationCodeService){
        this.userRepository = userRepository;
        this.verificationCodeService = verificationCodeService;
    }

    @Override
    public User signUp(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void updatePassword(String email, String newPassword, String verificationCode) {
        logger.info("Received request to update password for email: {}", email);
        logger.info("Received verification code: {}", verificationCode);

        if (verificationCodeService.verifyVerificationCode(email, verificationCode)) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                user.setPassword(newPassword);
                userRepository.save(user);
                logger.info("Password updated successfully for email: {}", email);
            } else {
                // Handle case when user with given email is not found
                logger.error("User not found for email: {}", email);
                throw new UserNotFoundException("User not found for email: " + email);
            }
        } else {
            // Handle case when verification code is invalid
            logger.error("Invalid verification code for email: {}", email);
            throw new RuntimeException("Invalid verification code");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("There is no user with username " + username);
        } else {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername().trim(),
                    user.getPassword().trim(),
                    grantedAuthorities);
        }
    }
}