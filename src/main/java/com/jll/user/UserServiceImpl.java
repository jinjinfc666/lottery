package com.jll.user;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.ReceiverBankCard;

@Service
@Transactional
public class UserServiceImpl implements UserService
{
	@Resource
	UserDao userDao;

	@Override
	public ReceiverBankCard queryReceiverBankCardByBank(String bankCode) {
		// TODO Auto-generated method stub
		return null;
	}
  
}
