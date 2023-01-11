package com.ytech.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ytech.entity.Item;
import com.ytech.entity.Order;
import com.ytech.entity.StockMovement;
import com.ytech.exception.OrderException;
import com.ytech.repository.ItemRepository;
import com.ytech.repository.OrderRepository;
import com.ytech.repository.StockMovementRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class StockMovementServiceImpl implements StockMovementService {

	@Autowired
	private StockMovementRepository stockMovementRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public List<StockMovement> findAll() {
		return stockMovementRepository.findAll();
	}

	@Override
	public Optional<StockMovement> findById(Long id) {
		return stockMovementRepository.findById(id);
	}

	@Override
	public StockMovement add(StockMovement stockMovement) {
		Item item = itemRepository.findById(stockMovement.getItem().getId()).orElseThrow();
		Order order = null;

		StockMovement stockMovementDB = new StockMovement();
		stockMovementDB.setCreationDate(LocalDateTime.now());
		stockMovementDB.setItem(item);
		stockMovementDB.setQuantity(stockMovement.getQuantity());

		// when a stock movement is created, the system should try to attribute it to an
		// order that isn't complete
		if (stockMovement.getOrder() != null && stockMovement.getOrder().getId() != null) {
			order = orderRepository.findById(stockMovement.getOrder().getId()).orElseThrow();

			// check if order that isn't complete
			if (Boolean.TRUE.equals(order.getIsCompleted())) {
				throw new OrderException("Order already completed exception");
			}
		} else {
			order = new Order();
			order.setCreationDate(LocalDateTime.now());
			order.setIsCompleted(false);
			order.setQuantity(stockMovement.getQuantity());
			order.setItem(item);

			orderRepository.save(order);
		}

		stockMovementDB.setOrder(order);
		return stockMovementRepository.save(stockMovementDB);
	}

	@Override
	public StockMovement update(Long id, StockMovement stockMovement) {
		StockMovement stockMovementDB = stockMovementRepository.findById(id)
				.orElseThrow(() -> new OrderException("StockMovement does not exist with id: " + id));

		stockMovementDB.setCreationDate(LocalDateTime.now());
		stockMovementDB.setQuantity(stockMovement.getQuantity());

		return stockMovementRepository.save(stockMovementDB);
	}

	@Override
	public void deleteById(Long id) {
		stockMovementRepository.deleteById(id);
	}
}
