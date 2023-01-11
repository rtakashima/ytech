package com.ytech.dto;

import lombok.Data;

@Data
public class OrderInputDto {

	private Long id;

	private Long itemId;

	/**
	 * Item quantity
	 */
	private Integer quantity;

	private Long stockMovementId;

}
