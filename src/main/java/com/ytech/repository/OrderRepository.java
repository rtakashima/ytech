package com.ytech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ytech.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
