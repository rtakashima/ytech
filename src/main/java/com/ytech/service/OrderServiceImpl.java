package com.ytech.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ytech.entity.Order;
import com.ytech.entity.StockMovement;
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
	public Order add(Long stockMovementId, Order order, Long userId) {
		Order orderDB = order;
		orderDB.setCreationDate(LocalDateTime.now());

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new OrderException("User does not exist with id: " + userId));

		if (stockMovementId != null) {
			StockMovement stockMovement = stockMovementRepository.findById(stockMovementId)
					.orElseThrow(() -> new OrderException("Stock Moviment does not exist with id: " + stockMovementId));

			orderDB = stockMovement.getOrder();
		} else {
			StockMovement stockMovement = new StockMovement();
			stockMovement.setCreationDate(orderDB.getCreationDate());
			stockMovement.setItem(orderDB.getItem());
			stockMovement.setQuantity(orderDB.getQuantity());
			stockMovement.setOrder(orderDB);

			List<StockMovement> stockMovements = new ArrayList<>();
			stockMovements.add(stockMovement);
			orderDB.setStockMovements(stockMovements);
		}

		orderDB.setIsCompleted(true);
		orderDB.setUser(user);

		// when an order is created, it should try to satisfy it with the current stock
		checkStockAvailable(orderDB);

		return orderRepository.save(orderDB);
	}

	@Override
	public Order update(Long id, Order order, Long userId) {
		Order orderDB = orderRepository.findById(id)
				.orElseThrow(() -> new OrderException("Order does not exist with id: " + id));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new OrderException("User does not exist with id: " + userId));

		orderDB.setCreationDate(LocalDateTime.now());
		orderDB.setIsCompleted(true);
		orderDB.setQuantity(order.getQuantity());
		orderDB.setUser(user);

		if (!orderDB.getItem().getId().equals(order.getItem().getId())) {
			throw new OrderException("Invalid item Id");
		}

		// when an order is created, it should try to satisfy it with the current stock
		checkStockAvailable(order);

		orderRepository.save(orderDB);

		// send e-mail
		emailService.sendSimpleMail(user.getEmail(), orderDB.getItem().getName(), order.getQuantity());

		return orderDB;
	}

	/**
	 * Check if the quantity is available in stock
	 * 
	 * @param order
	 */
	private void checkStockAvailable(Order order) {
		if (order == null || order.getItem() == null) {
			throw new OrderException("Invalid order item");
		}

		Long total = stockMovementRepository.findTotalItemsInStock(order.getItem().getId());

		if (total == null || order.getQuantity() > total) {
			throw new OrderException("Invalid item quantity");
		}
	}

	@Override
	public void deleteById(Long id) {
		orderRepository.deleteById(id);
	}
}
