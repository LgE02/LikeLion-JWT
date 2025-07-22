package com.example.week10_Jwt.repository;

import com.example.week10_Jwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
