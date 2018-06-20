package com.jll.pay.tlCloud;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.entity.MoneyInInfo;
import com.jll.entity.ReceiverBankCard;
import com.jll.entity.display.TlCloudNotices;
import com.jll.pay.order.DepositOrderService;
import com.jll.sys.config.ReceiverBankCardService;


@RestController
@RequestMapping({"/tlCloud"})
public class TlCouldController
{
  private Logger logger = Logger.getLogger(TlCouldController.class);
  
  @Resource
  TlCloudService tlCloudService;
  
  @Resource
  ReceiverBankCardService recBankCardService;
  
  @Resource
  DepositOrderService depositOrderService;
  
  private final String KEY_RESPONSE_STATUS = "success";
  
  @PostConstruct
  public void init() {
	  
  }
  
  @RequestMapping(value={"/orders"}, method={RequestMethod.POST}, produces={"application/json"})
  public Map<String, Object> saveDepositOrder(@RequestParam(name="userName",required = true) String userName,
		  @RequestParam(name="payerName",required = true) String payerName,
		  @RequestParam(name="payCardNumber",required = true) String payCardNumber,
		  @RequestParam(name="rechargeType",required = true) String rechargeType,
		  @RequestParam(name="amount",required = true) float amount)
  {
	  logger.debug("userName :: " + logger+"   payerName::"+payerName);
    Map<String, Object> ret = new HashMap<>();
    
    String comment = Utils.produce6DigitsCaptchaCode();
	Map<String, Object> params = new HashMap<>();
	params.put("userName", userName);
	params.put("payerName", payerName);
	params.put("rechargeType", rechargeType);
	params.put("amount", amount);
	params.put("comment", comment);
	params.put("payCardNumber", payCardNumber);
	
	String retCode = tlCloudService.saveDepositOrder(params);
	if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
		ReceiverBankCard recBankCard = recBankCardService.queryReceiverBankCardByBank(rechargeType);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		recBankCard.setComment(comment);
		ret.put("data", recBankCard);
	}else {
		ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
		ret.put(Message.KEY_ERROR_CODE, retCode);
		ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
	}
	
	return ret;
  } 
  
  
  @RequestMapping(value={"/orders/{orderId}"}, method={RequestMethod.DELETE}, produces={"application/json"})
  public Map<String, Object> cancelOrder(@PathVariable("orderId") String orderId)
  {
    Map<String, Object> ret = new HashMap<>();
	
	Map<String, Object> params = new HashMap<>();
	params.put("id", orderId);
	
	int orderIdInt = -1;
	try {
		orderIdInt = Integer.parseInt(orderId);
	}catch(NumberFormatException ex) {
		ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
		ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
		ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
		return ret;
	}
	
	MoneyInInfo depositOrder = depositOrderService.queryDepositOrderById(orderIdInt);
	if(depositOrder == null) {
		ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
		ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PAYMENT_DEPOSIT_ERROR_ORDER.getCode());
		ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PAYMENT_DEPOSIT_ERROR_ORDER.getErrorMes());
		return ret;
	}
	
    String retCode = tlCloudService.cancelOrder(orderIdInt);
    if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
	}else {
		ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
		ret.put(Message.KEY_ERROR_CODE, retCode);
		ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
	}
    
    return ret;
  }
  
  /**
   * payment platform will call this method to inform that the deposit record already in database.
   * next step , this method have to change the status of deposit order, and change the balance of user
 * @param userName
 * @return        {"success": true}
 */
@RequestMapping(value={"/notices"}, method={RequestMethod.POST}, consumes={"application/json"}, produces={"application/json"})
  public Map<String, Object> notices(@RequestBody TlCloudNotices notices,
		  HttpServletRequest request)
  {
    Map<String, Object> ret = new HashMap<>();
    
    String ip = request.getRemoteAddr();
    
    int orderIdInt = -1;
    
    if(notices == null 
    		|| notices.getOrder_id() == null 
    		|| notices.getOrder_id().length() == 0) {    	
    	
    	ret.put(KEY_RESPONSE_STATUS, false);
    	return ret;
    }
    
    try {
		orderIdInt = Integer.parseInt(notices.getOrder_id());
	}catch(NumberFormatException ex) {
		ret.put(KEY_RESPONSE_STATUS, false);
    	return ret;
	}
    
    boolean isAuthorized = tlCloudService.isAuthorized(ip);
    
    logger.info("The reuqest IP is ::: " + (ip == null?"null":ip) +"  isAuthroized ::: " + isAuthorized);
    
    if(!isAuthorized) {
    	ret.put(KEY_RESPONSE_STATUS, false);
    	return ret;
    }
    
    boolean isExisting = tlCloudService.isOrderExisting(orderIdInt);
    
    logger.info("The order ::: " + orderIdInt +"  isExisting :::" + isExisting);
    
    if(!isExisting) {
    	ret.put(KEY_RESPONSE_STATUS, false);
    	return ret;
    }
    
    tlCloudService.receiveDepositOrder(orderIdInt);
        
    ret.put(KEY_RESPONSE_STATUS, true);
    return ret;
  }
}
