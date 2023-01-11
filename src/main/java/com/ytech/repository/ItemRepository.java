package com.ytech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ytech.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
