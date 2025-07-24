package com.example.week10_Jwt.repository;

import com.example.week10_Jwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    //jpa구문 existisBy_ : 존재하는지 확인하는 쿼리
    Boolean existsByUsername(String username);

    //username을 받아 DB 테이블에서 회원을 조회하는 메소드 작성
    UserEntity findByUsername(String username);
}
