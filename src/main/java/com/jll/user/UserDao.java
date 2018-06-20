package com.jll.user;

import com.jll.entity.TbUsers;

public interface UserDao
{
	/**
	 * query the receiver bank card
	 * the bank card should be activated
	 * one bank only one bank account existing in the table
	 * @param bankCode
	 * @return
	 */
	void modifyUserInfo(TbUsers userInfo);
	
}
