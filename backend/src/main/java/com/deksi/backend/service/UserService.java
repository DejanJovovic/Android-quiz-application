package com.deksi.backend.service;

import com.deksi.backend.model.User;

public interface UserService {

    User signUp(User user);

    User findByUsername(String username);

    User findByEmail(String email);

    void updatePassword(String email, String newPassword, String verificationCode);

}
