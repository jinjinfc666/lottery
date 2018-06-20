package com.jll.sys.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
import com.jll.entity.ReceiverBankCard;

@RestController
@RequestMapping({"/configuration"})
public class SysConfigController
{
  private Logger logger = Logger.getLogger(SysConfigController.class);
  @Resource
  ReceiverBankCardService recBankCardService;
  
  @RequestMapping(value={"/online/bankAccounts"}, method={org.springframework.web.bind.annotation.RequestMethod.GET}, produces={"application/json"})
  public Map<String, Object> queryOnlineBankAccount()
  {
	Map<String, Object> ret = new HashMap<>();
    
    List<ReceiverBankCard> bankCards = recBankCardService.queryReceiverBankCard();
    ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
    ret.put("data", bankCards);
    return ret;
  }
}
