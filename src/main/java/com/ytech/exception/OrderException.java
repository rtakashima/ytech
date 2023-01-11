package com.ytech.exception;

import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OrderError error;

	public OrderException() {
		super();
	}

	public OrderException(String message) {
		super(message);
		error = new OrderError(message);
	}
}
