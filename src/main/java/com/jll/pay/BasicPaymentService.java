package com.jll.pay;

import java.util.Map;

public interface BasicPaymentService
{
	    
	  /**
	   * process the order that the money already in bank account.
	 * @param orderId
	 * @return
	 */
	String receiveDepositOrder(int orderId);
		
	/**
	 * sending the PlaceOrder request to tonglue cloud 
	 * @param response
	 * @return if success
	 */
	String saveDepositOrder(Map<String, Object> params);
}
