package com.coutinho.userservicemailsender.services;

import com.coutinho.userservicemailsender.model.User;

public interface UserService {

    User savedUser(User user);

    Boolean verifyToken(String token);
}
