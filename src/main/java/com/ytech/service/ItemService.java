package com.ytech.service;

import java.util.List;
import java.util.Optional;

import com.ytech.entity.Item;

public interface ItemService {

	List<Item> findAll();

	Optional<Item> findById(Long id);

	Item add(Item item);

	Item update(Long id, Item item);

	void deleteById(Long id);

}
