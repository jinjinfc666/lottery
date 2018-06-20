package com.jll.user;

import com.jll.entity.ReceiverBankCard;

public interface UserService
{
	ReceiverBankCard queryReceiverBankCardByBank(String bankCode);
}
