package com.jll.sys.config;

import java.util.List;

import com.jll.entity.ReceiverBankCard;
import com.jll.entity.TbBankback;

public interface ReceiverBankCardDao
{
	/**
	 * query the receiver bank card
	 * the bank card should be activated
	 * one bank only one bank account existing in the table
	 * @param bankCode
	 * @return
	 */
	ReceiverBankCard queryReceiverBankCardByBank(String bankCode);
	
	List<ReceiverBankCard> queryReceiverBankCard();
	
	void addDepositRecord(TbBankback record);
}
