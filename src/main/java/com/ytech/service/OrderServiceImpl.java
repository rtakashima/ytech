package com.ytech.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ytech.entity.Order;
import com.ytech.entity.User;
import com.ytech.exception.OrderException;
import com.ytech.repository.OrderRepository;
import com.ytech.repository.StockMovementRepository;
import com.ytech.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private StockMovementRepository stockMovementRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Override
	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	@Override
	public Optional<Order> findById(Long id) {
		return orderRepository.findById(id);
	}

	@Override
	public Order add(Order order) {
		return orderRepository.save(order);
	}

	@Override
	public Order update(Long id, Order order, Long userId) {
		Order orderDB = orderRepository.findById(id)
				.orElseThrow(() -> new OrderException("Order does not exist with id: " + id));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new OrderException("User does not exist with id: " + id));

		orderDB.setCreationDate(LocalDateTime.now());
		orderDB.setIsCompleted(true);
		orderDB.setQuantity(order.getQuantity());
		orderDB.setUser(user);

		if (!orderDB.getItem().getId().equals(order.getItem().getId())) {
			throw new OrderException("Invalid item Id");
		}

		// when an order is created, it should try to satisfy it with the current stock
		Long total = stockMovementRepository.findTotalItemsInStock(order.getItem().getId());

		if (total == null || order.getQuantity() > total) {
			throw new OrderException("Invalid item quantity");
		}

		orderRepository.save(orderDB);

		// send e-mail
		emailService.sendSimpleMail(user.getEmail(), orderDB.getItem().getName(), order.getQuantity());

		return orderDB;
	}

	@Override
	public void deleteById(Long id) {
		orderRepository.deleteById(id);
	}
}
