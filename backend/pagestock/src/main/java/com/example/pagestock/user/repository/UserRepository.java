package com.example.pagestock.user.repository;

import com.example.pagestock.user.dto.UserResponse;
import com.example.pagestock.user.model.User;
import org.hibernate.boot.models.annotations.internal.OptimisticLockAnnotation;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

interface UserRepository extends ListCrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
