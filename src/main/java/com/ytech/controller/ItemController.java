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

import com.ytech.dto.ItemDto;
import com.ytech.entity.Item;
import com.ytech.exception.OrderError;
import com.ytech.exception.OrderException;
import com.ytech.service.ItemService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takashima
 *
 */
@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {

	@Autowired
	private ItemService itemService;

	@Autowired
	private ModelMapper mapper;

	@GetMapping("/")
	public ResponseEntity<?> getItems() {
		log.info("Listing items");

		try {
			List<Item> items = itemService.findAll();
			List<ItemDto> itemsDto = items.stream().map(e -> mapper.map(e, ItemDto.class)).collect(Collectors.toList());

			return ResponseEntity.ok(itemsDto);
		} catch (OrderException e) {
			log.error("Item not found exception");
			return ResponseEntity.badRequest().body(new OrderError("Item not found exception"));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getItem(@PathVariable("id") Long id) {
		log.info("Getting user  " + id, id);

		try {
			Item item = itemService.findById(id).orElseThrow(() -> new OrderException("Item not found"));

			return ResponseEntity.ok(mapper.map(item, ItemDto.class));
		} catch (OrderException e) {
			log.error("Item not found exception", e);
			return ResponseEntity.badRequest().body(new OrderError("Item not found exception"));
		}
	}

	@PostMapping("/")
	public ResponseEntity<?> addItem(@RequestBody ItemDto itemDto) {
		log.info("Adding new item");

		try {
			Item item = mapper.map(itemDto, Item.class);
			return ResponseEntity.ok(mapper.map(itemService.add(item), ItemDto.class));
		} catch (OrderException e) {
			log.error("Item could not be saved");
			return ResponseEntity.badRequest().body(new OrderError("Item could not be saved"));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateItem(@PathVariable("id") Long id, @RequestBody ItemDto itemDto) {
		log.info("Updating item " + id);

		try {
			Item item = mapper.map(itemDto, Item.class);

			return ResponseEntity.ok(mapper.map(itemService.update(id, item), ItemDto.class));
		} catch (OrderException e) {
			log.error("Item could not be updated", e);
			return ResponseEntity.badRequest().body(new OrderError("Item could not be updated"));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteItem(@PathVariable("id") Long id) {
		log.info("Deleting item " + id);

		try {
			itemService.deleteById(id);

			return ResponseEntity.noContent().build();
		} catch (DataIntegrityViolationException | OrderException e) {
			log.error("Item could not be deleted", e);
			return ResponseEntity.badRequest().body(new OrderError("Item could not be deleted"));
		}
	}

}
