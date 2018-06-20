package com.jll.pay.caiPay;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.entity.display.CaiPayNotices;
import com.jll.entity.display.TlCloudNotices;
import com.jll.pay.order.DepositOrderService;
import com.jll.pay.tlCloud.TlCloudService;
import com.jll.sys.config.ReceiverBankCardService;


@RestController
@RequestMapping({"/caiPay"})
public class CaiPayController
{
	private Logger logger = Logger.getLogger(CaiPayController.class);
  
	@Resource
	CaiPayService caiPayService;
  
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
		  @RequestParam(name = "amount", required = true) float amount,
		  @RequestParam(name = "payMode", required = true) String payMode,
		  HttpServletRequest request)
  {
    Map<String, Object> ret = new HashMap<>();    
    Map<String, String> data = new HashMap<>();
    String comment = Utils.produce6DigitsCaptchaCode();
	Map<String, Object> params = new HashMap<>();
	params.put("userName", userName);
	params.put("rechargeType", payMode);
	params.put("amount", amount);
	params.put("comment", comment);
	params.put("reqHost", request.getServerName() +":"+ request.getServerPort());
	params.put("reqContext", request.getServletContext().getContextPath());
	String ip = request.getRemoteAddr();
	boolean isAuthorized = caiPayService.isAuthorized(ip);
	if(!isAuthorized) {
		logger.info("The reuqest IP is ::: " + (ip == null?"null":ip) +"  isAuthroized ::: " + isAuthorized);
		
		ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
		ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION.getCode());
		ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION.getErrorMes());
    	return ret;
    }
	
	String retCode = caiPayService.processScanPay(params);
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
  
  
  @RequestMapping(value={"/onlineBankPay"}, method={RequestMethod.GET}/*, produces={"application/json"}*/)
  public void onlineBankPay(@RequestParam(name = "userName") String userName,
		  @RequestParam(name = "amount", required = true) float amount,
		  @RequestParam(name = "payMode", required = true) String payMode,
		  @RequestParam(name = "accNoType") String accNoType,
		  @RequestParam(name = "accountType", required=true) String accountType,
		  @RequestParam(name = "bankCardNo") String bankCardNo,
		  @RequestParam(name = "phone") String phone,
		  @RequestParam(name = "idNo") String idNo,
		  @RequestParam(name = "expireDate") String expireDate,
		  @RequestParam(name = "cvn2") String cvn2,
		  @RequestParam(name = "tranChannel", required=true) String tranChannel,
		  HttpServletRequest request,
		  HttpServletResponse response)
  {
	  Map<String, Object> ret = new HashMap<>();
	  Map<String,Object> data = new HashMap<>();
	  
	  String ip = request.getRemoteAddr();
	  
	    String comment = Utils.produce6DigitsCaptchaCode();
		Map<String, Object> params = new HashMap<>();
		params.put("userName", userName);
		params.put("rechargeType", payMode);
		params.put("amount", amount);
		params.put("accNoType", accNoType);
		params.put("accountType", accountType);
		params.put("bankCardNo", bankCardNo);
		params.put("phone", phone);
		params.put("idNo", idNo);
		params.put("expireDate", expireDate);
		params.put("cvn2", cvn2);
		params.put("tranChannel", tranChannel);
		params.put("comment", comment);
		params.put("reqHost", request.getServerName() +":"+ request.getServerPort());
		params.put("reqContext", request.getServletContext().getContextPath());
		boolean isValid = caiPayService.isValid(params);
		
		if(!isValid) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	//return ret;
		}
		
		
		boolean isAuthorized = caiPayService.isAuthorized(ip);
		if(!isAuthorized) {
			logger.info("The reuqest IP is ::: " + (ip == null?"null":ip) +"  isAuthroized ::: " + isAuthorized);
			
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION.getErrorMes());
	    	//return ret;
	    }
		
		String retCode = caiPayService.processOnlineBankPay(params);
		
		try {
			response.getWriter().print((String)params.get("redirect"));
			response.getWriter().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.put("redirect", params.get("redirect"));
		ret.put("data", data);
		
		if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, retCode);
			ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
		}
		
		//return ret;
  } 
  
  /**
   * payment platform will call this method to inform that the deposit record already in database.
   * next step , this method have to change the status of deposit order, and change the balance of user
 * @param userName
 * @return        {"success": true}
 */
  @RequestMapping(value={"/notices/{noticeType}"}, method={RequestMethod.POST}, consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces={MediaType.TEXT_PLAIN_VALUE})
  public String scanPayNotices(@PathVariable("noticeType") int noticeType ,
		  CaiPayNotices notices,
		  HttpServletRequest request)
  {
    //Map<String, Object> ret = new HashMap<>();
    
    String ip = request.getRemoteAddr();
    
    int orderIdInt = -1;
    float orderAmount = -1;
    try {
    	orderIdInt = Integer.parseInt(notices.getPrdOrdNo());
    	orderAmount = Float.parseFloat(notices.getOrderAmount());
    }catch(NumberFormatException ex) {
    	return "FAIL";
    }
    
    if(!caiPayService.isNoticesValid(notices, noticeType)) {
    	return "FAIL";
    }
    
    
    boolean isAuthorized = caiPayService.isAuthorized(ip);
    
    logger.info("The reuqest IP is ::: " + (ip == null?"null":ip) +"  isAuthroized ::: " + isAuthorized);
    
    if(!isAuthorized) {
    	//ret.put(KEY_RESPONSE_STATUS, false);
    	return "FAIL";
    }
    
    boolean isExisting = caiPayService.isOrderExisting(orderIdInt);
    
    logger.info("The order ::: " + orderIdInt +"  isExisting :::" + isExisting);
    
    if(!isExisting) {
    	return "FAIL";
    }
    
    caiPayService.receiveDepositOrder(orderIdInt, orderAmount);
        
    return "SUCCESS";
  }
  
  /**
   * payment platform will call this method to inform that the deposit record already in database.
   * next step , this method have to change the status of deposit order, and change the balance of user
 * @param userName
 * @return        {"success": true}
 */
  @RequestMapping(value={"/onlineBankPayNotices/{noticeType}"}, method={RequestMethod.POST}, consumes={"application/json"}, produces={"application/json"})
  public Map<String, Object> onlineBankPayNotices(@PathVariable("noticeType") int noticeType ,
		  @RequestBody TlCloudNotices notices,
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
    
    boolean isAuthorized = caiPayService.isAuthorized(ip);
    
    logger.info("The reuqest IP is ::: " + (ip == null?"null":ip) +"  isAuthroized ::: " + isAuthorized);
    
    if(!isAuthorized) {
    	ret.put(KEY_RESPONSE_STATUS, false);
    	return ret;
    }
    
    boolean isExisting = caiPayService.isOrderExisting(orderIdInt);
    
    logger.info("The order ::: " + orderIdInt +"  isExisting :::" + isExisting);
    
    if(!isExisting) {
    	ret.put(KEY_RESPONSE_STATUS, false);
    	return ret;
    }
    
    caiPayService.receiveDepositOrder(orderIdInt);
        
    ret.put(KEY_RESPONSE_STATUS, true);
    return ret;
  }
}
