package com.jll.pay.tlCloud;

import com.jll.pay.BasicPaymentService;

public interface TlCloudService extends BasicPaymentService
{
	  /**
	   * if the remote client is authorized, it means if the ip locates in white list
	 * @param ip the remote client ip
	 * @return
	 */
	boolean isAuthorized(String ip);  
	  
	
	/**
	 * checking if the specified order is existing in database
	 * @param orderId
	 * @return
	 */
	boolean isOrderExisting(int orderId);
		
	String cancelOrder(int orderId);
}
