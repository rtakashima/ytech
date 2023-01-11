package com.ytech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ytech.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByEmail(String email);

}
