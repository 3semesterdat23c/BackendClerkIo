package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserId(int userId);

    Optional<User> findByUserEmail(String email);
    boolean existsByUserEmail(String email);
    boolean existsByUserId(int userId);

    Optional<User> findUserByUserId(int userId);
}