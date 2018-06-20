package com.jll.pay.order;

import java.util.Date;

import com.jll.entity.MoneyInInfo;

public abstract interface DepositOrderDao
{
	/**
	 * save the deposit order
	 * @param userName
	 * @param rechargeType 
	 * @param amount
	 * @return
	 */
	MoneyInInfo saveDepositOrder(String userName, String rechargeType, float amount, String comment, Date createTime);
	
	/**
	 * update the deposit order
	 * @param depositOrder
	 */
	void updateDepositOrder(MoneyInInfo depositOrder);
	
	MoneyInInfo queryDepositOrderById(int orderId);
}
