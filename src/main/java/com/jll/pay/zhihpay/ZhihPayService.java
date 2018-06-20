package com.jll.pay.zhihpay;

import java.util.Map;

import com.jll.entity.display.CaiPayNotices;
import com.jll.pay.BasicPaymentService;

public interface ZhihPayService extends BasicPaymentService
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
	
	/**
	 * @param params
	 * @return
	 */
	String processScanPay(Map<String, Object> params);
	
	/**
	 * @param params
	 * @return
	 */
	String processOnlineBankPay(Map<String, Object> params);
	
	/**
	 * @param params
	 * @return
	 */
	String receiveNoties(Map<String, Object> params);


	boolean isValid(Map<String, Object> params);
	
	boolean isNoticesValid(CaiPayNotices notices, int noticeType);	
	
	/**
	 * process the order that the money already in bank account.
	 * @param orderId 
	 * @param amount real amount that was deposited to bank account
	 * @return
	 */
	String receiveDepositOrder(int orderId, float amount);
}
