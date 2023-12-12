package com.auth.service.service;

import com.auth.service.entity.Users;
import com.auth.service.repository.UserCredentialRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl {

    @Autowired
    private UserCredentialRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    public String saveUser(Users credential) {
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        credential.setEmail(credential.getEmail());
        credential.setName(credential.getName());
        credential.setPhone_number(credential.getPhone_number());
        repository.save(credential);
        return "user added to the system";
    }

    public String updateUser(Users user){

        Optional<Users> optionalUser = repository.findById(user.getId());

        if (optionalUser.isPresent()) {
            // Modify the fields of the entity object
            Users updateUser = optionalUser.get();
            updateUser.setName(user.getName());
            updateUser.setPhone_number(user.getPhone_number());
            updateUser.setEmail(user.getEmail());
            updateUser.setPassword(user.getPassword());
            // Save the entity
            repository.save(updateUser);
        }

        return "user is updated";
    }

    public Optional<Users> getUserById(String email){
        Optional<Users> user = repository.findByEmail(email);

        return user;
    }

    public boolean checkUserEmail(String email){
        boolean exists = false;
        Optional<Users> user = repository.findByEmail(email);
        if(user.isPresent()){
            exists = true;
        }
        return exists;
    }

    public String generateToken(String name) {
        return jwtService.generateToken(name);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }


}