package com.ytech.service;

import java.util.List;
import java.util.Optional;

import com.ytech.entity.StockMovement;

public interface StockMovementService {

	List<StockMovement> findAll();

	Optional<StockMovement> findById(Long id);

	StockMovement add(StockMovement stockMovement);

	StockMovement update(Long id, StockMovement stockMovement);

	void deleteById(Long id);

}
