package com.ytech.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderOutputDto {

	private Long id;

	private Long itemId;

	private String itemName;
	/**
	 * Item quantity
	 */
	private Integer quantity;

	private Boolean isCompleted;

	private UserDto user;

	private List<StockMovementDto> stockMovements;
}
