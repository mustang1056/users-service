package com.auth.service.service;

import com.auth.service.entity.Users;

import java.util.Optional;

public interface AuthService {

    String saveUser(Users credential);

    String updateUser(Users user);

    Optional<Users> getUserById(String email);

    boolean checkUserEmail(String email);

    String generateToken(String name);

    void validateToken(String token);


}
