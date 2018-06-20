package com.jll.pay.order;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.MoneyInInfo;

@Service
@Transactional
public class DepositOrderServiceImpl implements DepositOrderService
{
	@Resource
	DepositOrderDao depositOrderDao;

	@Override
	public MoneyInInfo queryDepositOrderById(int orderId) {
		MoneyInInfo depositOrder = depositOrderDao.queryDepositOrderById(orderId);
		return depositOrder;
	}
  
}
