package com.ytech.service;

import java.util.List;
import java.util.Optional;

import com.ytech.entity.User;

public interface UserService {

	List<User> findAll();

	Optional<User> findById(Long id);

	User add(User user);

	User update(Long id, User user);

	void deleteById(Long id);
}
