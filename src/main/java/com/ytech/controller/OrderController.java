/**
 * 
 */
package com.ytech.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ytech.dto.OrderInputDto;
import com.ytech.dto.OrderOutputDto;
import com.ytech.entity.Order;
import com.ytech.exception.OrderError;
import com.ytech.exception.OrderException;
import com.ytech.service.OrderService;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author takashima
 *
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private ModelMapper mapper;

	@GetMapping("/")
	public ResponseEntity<?> getOrders() {
		log.info("Listing orders");

		try {
			List<Order> orders = orderService.findAll();
			List<OrderOutputDto> ordersDto = orders.stream().map(e -> mapper.map(e, OrderOutputDto.class))
					.collect(Collectors.toList());
			return ResponseEntity.ok(ordersDto);
		} catch (OrderException e) {
			log.error("Order not found exception", e);
			return ResponseEntity.badRequest().body(new OrderError("Order not found eception"));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getOrder(@PathVariable("id") Long id) {
		log.info("Getting user " + id);

		try {
			Order order = orderService.findById(id).orElseThrow(() -> new OrderException("Order not found"));

			return ResponseEntity.ok(mapper.map(order, OrderOutputDto.class));
		} catch (OrderException e) {
			log.error("Order not found exception", e);
			return ResponseEntity.badRequest().body(new OrderError("Order not found exception"));
		}
	}

	@PostMapping("/")
	public ResponseEntity<?> addOrder(@RequestHeader HttpHeaders header, @RequestBody OrderInputDto orderDto) {
		log.info("Adding new order");

		try {
			Order order = orderService.add(mapper.map(orderDto, Order.class));
			OrderOutputDto dto = mapper.map(order, OrderOutputDto.class);
			log.info("Order added " + dto.getId());

			return ResponseEntity.ok(dto);
		} catch (OrderException e) {
			log.error("Order could not be saved", e);
			return ResponseEntity.badRequest().body(new OrderError("Order could not be saved"));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateOrder(@RequestHeader HttpHeaders header, @PathVariable("id") Long id,
			@RequestBody OrderInputDto orderDto) {
		log.info("Updating order " + id);

		try {
			Order order = orderService.update(id, mapper.map(orderDto, Order.class), getUserId(header));
			log.info("Order completed " + order.getId());

			return ResponseEntity.ok(mapper.map(order, OrderOutputDto.class));
		} catch (OrderException e) {
			log.error("Order could not be updated", e);
			return ResponseEntity.badRequest().body(new OrderError("Order could not be updated"));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteOrder(@PathVariable("id") Long id) {
		log.info("Deleting order " + id);

		try {
			orderService.deleteById(id);

			return ResponseEntity.noContent().build();
		} catch (DataIntegrityViolationException | OrderException e) {
			log.error("Order could not be deleted", e);
			return ResponseEntity.badRequest().body(new OrderError("Order could not be deleted"));
		}
	}

	private Long getUserId(HttpHeaders header) {
		try {
			if (header != null && !CollectionUtils.isEmpty(header.get("userid"))) {
				String userid = header.get("userid").get(0);
				return !StringUtils.isBlank(userid) ? Long.valueOf(userid) : null;
			}
		} catch (OrderException e) {
			log.error("Invalid User Id");
		}

		return null;
	}

}
