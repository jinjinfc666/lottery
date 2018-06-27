package com.jll.pay.zhihpay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.entity.display.ZhihPayNotices;
import com.jll.pay.order.DepositOrderService;
import com.jll.pay.tlCloud.TlCloudService;
import com.jll.sys.config.ReceiverBankCardService;


@RestController
@RequestMapping({"/zhihPay"})
public class ZhihPayController
{
	private Logger logger = Logger.getLogger(ZhihPayController.class);
  
	@Resource
	ZhihPayService zhihPayService;
  
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
  
  @RequestMapping(value={"/scanPay"}, method={RequestMethod.POST}, produces={"application/json"})
  public Map<String, Object> scanPay(@RequestParam(name = "userName", required = true) String userName,
		  @RequestParam(name = "serviceType", required = true) String serviceType,
		  @RequestParam(name = "orderAmount", required = true) float orderAmount,
		  HttpServletRequest request)
  {
    Map<String, Object> ret = new HashMap<>();    
    Map<String, String> data = new HashMap<>();
    String comment = Utils.produce6DigitsCaptchaCode();
	Map<String, Object> params = new HashMap<>();
	params.put("userName", userName);
	params.put("rechargeType", serviceType);
	params.put("amount", orderAmount);
	params.put("comment", comment);
	params.put("reqHost", request.getServerName() +":"+ request.getServerPort());
	params.put("reqContext", request.getServletContext().getContextPath());
	params.put("reqIP", request.getRemoteAddr());
	String ip = request.getRemoteAddr();
	boolean isAuthorized = zhihPayService.isAuthorized(ip);
	if(!isAuthorized) {
		logger.info("The reuqest IP is ::: " + (ip == null?"null":ip) +"  isAuthroized ::: " + isAuthorized);
		
		ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
		ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION.getCode());
		ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION.getErrorMes());
    	return ret;
    }
	
	String retCode = zhihPayService.processScanPay(params);
	if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
		String qrCode = params.get("qrcode") == null?null:(String)params.get("qrcode");
		if(qrCode == null || qrCode.length() == 0) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, retCode);
			ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
		}
		data.put("qrcode", qrCode);
		ret.put("data", data);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
	}else {
		ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
		ret.put(Message.KEY_ERROR_CODE, retCode);
		ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
	}
	
	return ret;
  } 
  
  
  @RequestMapping(value={"/onlineBankPay"}, method={RequestMethod.POST}, produces={"application/json"})
  public Map<String,Object> onlineBankPay(@RequestParam(name = "userName") String userName,
		  @RequestParam(name = "orderAmount", required = true) float orderAmount,
		  HttpServletRequest request,
		  HttpServletResponse response)
  {
	  Map<String, Object> ret = new HashMap<>();
	  Map<String,Object> data = new HashMap<>();
	  
	  String ip = request.getRemoteAddr();
	  
	    String comment = Utils.produce6DigitsCaptchaCode();
		Map<String, Object> params = new HashMap<>();
		params.put("userName", userName);
		params.put("rechargeType", Constants.ZHIH_PAY_MODE.DIRECT_ONLINE_BANK.getCode());
		params.put("amount", orderAmount);
		params.put("comment", comment);
		if(request.getServerPort() == 80) {
			params.put("reqHost", request.getServerName());
		}else {
			params.put("reqHost", request.getServerName() +":"+ request.getServerPort());
		}
		params.put("reqContext", request.getServletContext().getContextPath());
		/*boolean isValid = zhihPayService.isValid(params);
		
		if(!isValid) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	//return ret;
		}*/
		
		
		boolean isAuthorized = zhihPayService.isAuthorized(ip);
		if(!isAuthorized) {
			logger.info("The reuqest IP is ::: " + (ip == null?"null":ip) +"  isAuthroized ::: " + isAuthorized);
			
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION.getErrorMes());
	    	return ret;
	    }
		
		String retCode = zhihPayService.processOnlineBankPay(params);
					
		if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", params);
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
  @RequestMapping(value={"/notices/{noticeType}"}, method={RequestMethod.POST}, consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces={MediaType.TEXT_PLAIN_VALUE})
  public String notices(@PathVariable("noticeType") int noticeType ,
		  ZhihPayNotices notices,
		  HttpServletRequest request)
  {
    String ip = request.getRemoteAddr();
    int orderIdInt = -1;
    float orderAmount = -1;
    try {
    	orderIdInt = Integer.parseInt(notices.getOrder_no());
    	orderAmount = Float.parseFloat(notices.getOrder_amount());
    }catch(NumberFormatException ex) {
    	return "FAIL";
    }
    
    boolean isAuthorized = zhihPayService.isAuthorized(ip);
    
    logger.info("The reuqest IP is ::: " + (ip == null?"null":ip) +"  isAuthroized ::: " + isAuthorized);
    
    if(!isAuthorized) {
    	//ret.put(KEY_RESPONSE_STATUS, false);
    	return "FAIL";
    }
    
    if(!zhihPayService.isNoticesValid(notices, noticeType)) {
    	logger.info("isNoticesValid ::: " + zhihPayService.isNoticesValid(notices, noticeType));
    	return "FAIL";
    }
    
    boolean isExisting = zhihPayService.isOrderExisting(orderIdInt);
    
    logger.info("The order ::: " + orderIdInt +"  isExisting :::" + isExisting);
    
    if(!isExisting) {
    	return "FAIL";
    }
    
    boolean isSuccess = zhihPayService.isOrderSuccess(notices);
    if(!isSuccess) {
    	return "SUCCESS";
    }
    
    boolean isNotified = zhihPayService.isOrderNotified(notices);
    if(isNotified) {
    	return "SUCCESS";
    }
    
    zhihPayService.receiveDepositOrder(orderIdInt, orderAmount);
        
    return "SUCCESS";
  }
}
