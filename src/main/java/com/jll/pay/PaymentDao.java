package com.jll.pay;

import com.jll.entity.MoneyInInfo;
import com.jll.entity.TbBankback;
import com.jll.entity.TbUsers;

public abstract interface PaymentDao
{
  public abstract long queryDepositTimes(String paramString);
  
	/**
	 * query the specified deposit order 
	 * @param orderId the id or deposit order
	 * @return
	 */
	MoneyInInfo queryDepositOrder(String orderId);
	 
  TbUsers queryUserInfo(String userName);
  
}
