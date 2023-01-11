package com.ytech.dto;

import lombok.Data;

@Data
public class StockMovementDto {

	private Long id;
	private Long itemId;
	private Integer quantity;
	private Long orderId;

}
