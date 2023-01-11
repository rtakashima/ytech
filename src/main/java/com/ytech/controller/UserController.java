/**
 * 
 */
package com.ytech.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ytech.dto.UserDto;
import com.ytech.entity.User;
import com.ytech.exception.OrderError;
import com.ytech.exception.OrderException;
import com.ytech.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takashima
 *
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ModelMapper mapper;

	@GetMapping("/")
	public ResponseEntity<?> getUsers() {
		log.info("Listing users");

		try {
			List<User> users = userService.findAll();
			List<UserDto> usersDto = users.stream().map(e -> mapper.map(e, UserDto.class)).collect(Collectors.toList());

			return ResponseEntity.ok(usersDto);
		} catch (OrderException e) {
			log.error("User not found exception", e);
			return ResponseEntity.badRequest().body(new OrderError("User not found exception"));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
		log.info("Getting user " + id);

		try {
			User user = userService.findById(id).orElseThrow(() -> new OrderException("User not found"));

			return ResponseEntity.ok(mapper.map(user, UserDto.class));
		} catch (OrderException e) {
			log.error("User not found exception", e);
			return ResponseEntity.badRequest().body(new OrderError("User not found exception"));
		}
	}

	@PostMapping("/")
	public ResponseEntity<?> addUser(@RequestBody UserDto userDto) {
		log.info("Adding new user");

		try {
			User user = mapper.map(userDto, User.class);
			return ResponseEntity.ok(mapper.map(userService.add(user), UserDto.class));
		} catch (OrderException e) {
			log.error("User could not be saved", e);
			return ResponseEntity.badRequest().body(new OrderError("User could not be saved"));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
		log.info("Updating user %d ", id);

		try {
			User user = mapper.map(userDto, User.class);
			return ResponseEntity.ok(mapper.map(userService.update(id, user), UserDto.class));
		} catch (OrderException e) {
			log.error("User could not be updated", e);
			return ResponseEntity.badRequest().body(new OrderError("User could not be updated"));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
		log.info("Deleting user %d ", id);

		try {
			userService.deleteById(id);

			return ResponseEntity.noContent().build();
		} catch (DataIntegrityViolationException | OrderException e) {
			log.error("User could not be deleted", e);
			return ResponseEntity.badRequest().body(new OrderError("User could not be deleted"));
		}
	}

}
