package com.jll.pay.order;

import com.jll.entity.MoneyInInfo;

public interface DepositOrderService
{
	MoneyInInfo queryDepositOrderById(int orderId);
}
