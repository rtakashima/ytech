package com.ytech.service;

import java.util.List;
import java.util.Optional;

import com.ytech.entity.Order;

public interface OrderService {

	List<Order> findAll();

	Optional<Order> findById(Long id);

	Order add(Order order);

	Order update(Long id, Order order, Long userId);

	void deleteById(Long id);

}
