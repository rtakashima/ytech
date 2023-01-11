package com.ytech.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ytech.entity.Item;
import com.ytech.exception.OrderException;
import com.ytech.repository.ItemRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemRepository itemRepository;

	@Override
	public List<Item> findAll() {
		return itemRepository.findAll();
	}

	@Override
	public Optional<Item> findById(Long id) {
		return itemRepository.findById(id);
	}

	@Override
	public Item add(Item item) {
		return itemRepository.save(item);
	}

	@Override
	public Item update(Long id, Item item) {
		Item itemDB = itemRepository.findById(id)
				.orElseThrow(() -> new OrderException("Item not exist with id: " + id));

		itemDB.setName(item.getName());

		return itemRepository.save(itemDB);
	}

	@Override
	public void deleteById(Long id) {
		itemRepository.deleteById(id);
	}
}
