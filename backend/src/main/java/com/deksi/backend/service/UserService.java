package com.deksi.backend.service;

import com.deksi.backend.model.User;

public interface UserService {

    User signUp(User user);

    User findByUsername(String username);
}
