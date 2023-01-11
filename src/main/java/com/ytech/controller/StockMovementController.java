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

import com.ytech.dto.StockMovementDto;
import com.ytech.entity.StockMovement;
import com.ytech.exception.OrderError;
import com.ytech.exception.OrderException;
import com.ytech.service.StockMovementService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takashima
 *
 */
@Slf4j
@RestController
@RequestMapping("/stockMovement")
public class StockMovementController {

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private StockMovementService stockMovementService;

	@GetMapping("/")
	public ResponseEntity<?> getStockMovements() {
		log.info("Listing stockMovements");

		try {
			List<StockMovement> stockMovements = stockMovementService.findAll();
			List<StockMovementDto> stockMovementsDto = stockMovements.stream()
					.map(e -> mapper.map(e, StockMovementDto.class)).collect(Collectors.toList());
			return ResponseEntity.ok(stockMovementsDto);
		} catch (OrderException e) {
			log.error("StockMovement not found exception", e);
			return ResponseEntity.badRequest().body(new OrderError("StockMovement not found exception"));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getStockMovement(@PathVariable("id") Long id) {
		log.info("Getting stockMovement " + id);

		try {
			StockMovement stockMovement = stockMovementService.findById(id)
					.orElseThrow(() -> new OrderException("StockMovement not found"));

			return ResponseEntity.ok(mapper.map(stockMovement, StockMovementDto.class));
		} catch (OrderException e) {
			log.error("StockMovement not found exception", e);
			return ResponseEntity.badRequest().body(new OrderError("StockMovement not found exception"));
		}
	}

	@PostMapping("/")
	public ResponseEntity<?> addStockMovement(@RequestBody StockMovementDto stockMovementDto) {
		log.info("Adding new stockMovement");

		try {
			StockMovement stockMovement = mapper.map(stockMovementDto, StockMovement.class);
			StockMovementDto dto = mapper.map(stockMovementService.add(stockMovement), StockMovementDto.class);

			return ResponseEntity.ok(dto);
		} catch (OrderException e) {
			log.error("StockMovement could not be saved", e);
			return ResponseEntity.badRequest().body(new OrderError("StockMovement could not be saved"));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateStockMovement(@PathVariable("id") Long id,
			@RequestBody StockMovementDto stockMovementDto) {
		log.info("Updating stockMovement %d ", id);

		try {
			stockMovementService.update(id, mapper.map(stockMovementDto, StockMovement.class));

			return ResponseEntity.ok(stockMovementDto);
		} catch (OrderException e) {
			log.error("StockMovement could not be updated", e);
			return ResponseEntity.badRequest().body(new OrderError("StockMovement could not be updated"));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteStockMovement(@PathVariable("id") Long id) {
		log.info("Deleting stockMovement %d ", id);

		try {
			stockMovementService.deleteById(id);

			return ResponseEntity.noContent().build();
		} catch (DataIntegrityViolationException | OrderException e) {
			log.error("StockMovement could not be deleted", e);
			return ResponseEntity.badRequest().body(new OrderError("StockMovement could not be deleted"));
		}
	}

}
