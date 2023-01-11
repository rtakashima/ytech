package com.ytech.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ytech.entity.User;
import com.ytech.exception.OrderException;
import com.ytech.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public User add(User user) {
		return userRepository.save(user);
	}

	@Override
	public User update(Long id, User user) {
		User userDB = userRepository.findById(id)
				.orElseThrow(() -> new OrderException("User not exist with id: " + id));

		userDB.setEmail(user.getEmail());
		userDB.setName(user.getName());

		return userRepository.save(userDB);
	}

	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

}
