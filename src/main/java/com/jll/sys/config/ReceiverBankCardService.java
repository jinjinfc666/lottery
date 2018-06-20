package com.jll.sys.config;

import java.util.List;

import com.jll.entity.ReceiverBankCard;

public interface ReceiverBankCardService
{
	ReceiverBankCard queryReceiverBankCardByBank(String bankCode);
	
	List<ReceiverBankCard> queryReceiverBankCard();
}
