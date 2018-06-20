package com.jll.sys.config;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.ReceiverBankCard;

@Service
@Transactional
public class ReceiverBankCardServiceImpl implements ReceiverBankCardService
{
  @Resource
  ReceiverBankCardDao recBankCardDao;

	@Override
	public ReceiverBankCard queryReceiverBankCardByBank(String bankCode) {
		return recBankCardDao.queryReceiverBankCardByBank(bankCode);
	}

	@Override
	public List<ReceiverBankCard> queryReceiverBankCard() {
		return recBankCardDao.queryReceiverBankCard();
	}
  
}
