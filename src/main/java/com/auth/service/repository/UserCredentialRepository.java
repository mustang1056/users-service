package com.auth.service.repository;

import com.auth.service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository  extends JpaRepository<Users,Integer> {
    Optional<Users> findByEmail(String name);
}