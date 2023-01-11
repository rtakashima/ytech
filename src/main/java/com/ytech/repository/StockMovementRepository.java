package com.ytech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ytech.entity.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

	@Query("SELECT SUM(sm.quantity) FROM StockMovement sm WHERE sm.item.id = :itemId ")
	Long findTotalItemsInStock(@Param("itemId") Long itemId);
}
