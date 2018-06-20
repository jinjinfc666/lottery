package com.jll.pay.tlCloud;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.http.HttpRemoteStub;
import com.jll.common.utils.Utils;
import com.jll.entity.MoneyInInfo;
import com.jll.entity.ReceiverBankCard;
import com.jll.entity.TbBankback;
import com.jll.entity.TbUsers;
import com.jll.pay.PaymentDao;
import com.jll.pay.order.DepositOrderDao;
import com.jll.sys.config.ReceiverBankCardDao;
import com.jll.user.UserDao;

@Configuration
@PropertySource("classpath:tonglueCloud.properties")
@Service
@Transactional
public class TlCloudServiceImpl implements TlCloudService
{
	@Resource
	TlCloudDao tlCloudDao;
	
	@Resource
	PaymentDao payDao;
	
	@Resource
	DepositOrderDao depositOrderDao;
	
	@Resource
	ReceiverBankCardDao recBankCardDao;
	
	@Resource
	UserDao userDao;
	
	@Value("${auth.api-key}")
	private String apiKey;
	  
	@Value("${api.server1}")
	private String apiServer;
	  
	@Value("${api.place_order}")
	private String apiPlaceOrder;
	  
	@Value("${api.revoke_order}")
	private String apiRevokeOrder;
	  
	@Value("${api.list_order}")
	private String apiListOrder;
  
	@Override
	public boolean isAuthorized(String ip) {
		String roleName = "tong_lue_cloud";
		long count = tlCloudDao.queryWhiteListCount(ip, roleName);
		
		return count == 0?false:true;
	}


	@Override
	public String receiveDepositOrder(int orderId) {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeNum = format.format(new Date());
		
		MoneyInInfo depositOrder = depositOrderDao.queryDepositOrderById(orderId);
		TbUsers user  = payDao.queryUserInfo(depositOrder.getUserID());
		if(user == null) {
			return Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode();
		}
		
		BigDecimal userMoney = new BigDecimal(user.getUserMoney());
		BigDecimal allMoney = new BigDecimal(user.getAllMoney());
		BigDecimal inAmount = new BigDecimal(depositOrder.getAmount());
		
		TbBankback depositRecord = new TbBankback();
		depositRecord.setActivityType(0);
		depositRecord.setAddTime(new Date());
		depositRecord.setBackAfter(userMoney.add(inAmount).floatValue());
		depositRecord.setBackBefor(user.getUserMoney());
		depositRecord.setBackMoney(depositOrder.getAmount());
		depositRecord.setBackType("存款");
		depositRecord.setBackTypeText("系统充值");
		//depositRecord.setBankUser(bankUser);
		depositRecord.setFollows("网银充值成功-" + depositOrder.getRechargeType());
		//depositRecord.setId(id);
		depositRecord.setIfAutoTransfer(0);
		depositRecord.setIfDeal(0);
		depositRecord.setState("付款成功");
		depositRecord.setTradeNum(tradeNum +"-"+ orderId);
		depositRecord.setUserName(user.getUserName());
		depositRecord.setWebSite("JZ");
		recBankCardDao.addDepositRecord(depositRecord);
		
		
		user.setUserMoney(userMoney.add(inAmount).floatValue());
		user.setAllMoney(allMoney.add(inAmount).floatValue());
		userDao.modifyUserInfo(user);
		
		depositOrder.setStatus(Constants.DepositOrderState.END_ORDER.getCode());
		depositOrder.setDealTime(new Date());
		depositOrderDao.updateDepositOrder(depositOrder);
		
		return String.valueOf(Message.status.SUCCESS.getCode());
	}


	@Override
	public boolean isOrderExisting(int orderId) {
		long count = tlCloudDao.queryDepositOrderCount(orderId);
		return count == 0?false:true;
	}

	@Override
	public String saveDepositOrder(Map<String, Object> params) {
		Date createTime = new Date();
		String userName = (String)params.get("userName");
		String rechargeType = (String)params.get("rechargeType");
		float amount = (Float)params.get("amount");
		String comment = (String)params.get("comment");	
		String payCardNumber = (String)params.get("payCardNumber");
		String payerName = (String)params.get("payerName");
		
		Map<String, Object> pushParams = new HashMap<>();
		Map<String, String> reqHeaders = new HashMap<>();
		URI url = null;
		boolean isSuccess = true;
		
		MoneyInInfo depositOrder = depositOrderDao.saveDepositOrder(userName, rechargeType, amount, comment, createTime);
		ReceiverBankCard recBankCard = recBankCardDao.queryReceiverBankCardByBank(rechargeType);
		if(recBankCard == null) {
			return Message.Error.ERROR_SYSTEM_CONFIG_NO_RECEIVER_BANK_CARD.getCode();
		}
		reqHeaders.put("Content-Type", "application/json");
		
		pushParams.put("pay_card_number", payCardNumber);
		pushParams.put("pay_username", payerName);
		pushParams.put("order_id", String.valueOf(depositOrder.getRecordID()));
		pushParams.put("bank_flag", rechargeType);
		pushParams.put("card_number", recBankCard.getBankNum());
		pushParams.put("amount", amount);
		pushParams.put("create_time", createTime);
		pushParams.put("comment", comment);
		pushParams.put("apikey", apiKey);
		
		 
		 try {
		 	url = new URI(apiServer + apiPlaceOrder);
		 } catch (URISyntaxException e) {
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_CONFIGURATION.getCode();
		 }

		 Map<String, Object> response = HttpRemoteStub.synPost(url, reqHeaders, pushParams);
		    
		 isSuccess = isResponseSuccess(response);
		 
		 depositOrder.setOrderNumber(String.valueOf(depositOrder.getRecordID()));
		 if(isSuccess) {
			 depositOrderDao.updateDepositOrder(depositOrder);
		 	return String.valueOf(Message.status.SUCCESS.getCode());
		 }else {
			 //failed to push 
			 depositOrder.setStatus(Constants.DepositOrderState.FAILED_PUSH.getCode());
			 depositOrderDao.updateDepositOrder(depositOrder);
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER.getCode();
		 }
	}


	@Override
	public String cancelOrder(int orderId) {
		Map<String,Object> params = new HashMap<>();
		Map<String, String> reqHeaders = new HashMap<>();
		reqHeaders.put("Content-Type", "application/json");
		
		URI url;
		 try {
		 	url = new URI(apiServer + apiRevokeOrder);
		 } catch (URISyntaxException e) {
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_CONFIGURATION.getCode();
		 }

		 params.put("apikey", apiKey);
		 params.put("id", orderId);
		 
		 Map<String, Object> response = HttpRemoteStub.synPost(url, reqHeaders, params);
		    
		 boolean isSuccess = isResponseSuccess(response);
		    
		 if(isSuccess) {
			 MoneyInInfo depositOrder = depositOrderDao.queryDepositOrderById(orderId);
			 depositOrder.setStatus(Constants.DepositOrderState.CANCEL_ORDER.getCode());
			 depositOrderDao.updateDepositOrder(depositOrder);
		 	return String.valueOf(Message.status.SUCCESS.getCode());
		 }else {
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER.getCode();
		 }
	}
	
	private boolean isResponseSuccess(Map<String, Object> response) {
		int status = (int)response.get("status");
		if(status != HttpStatus.SC_OK) {
			return false;
		}else {
			String body = (String)response.get("responseBody");
			if(body != null && body.length() > 0) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					JsonNode node = mapper.readTree(body.getBytes());
					if(node == null) {
						return false;
					}
					
					node = node.get("success");
					if(node == null) {
						return false;
					}
					
					boolean isSuccess = node.asBoolean();
					return isSuccess;
				} catch (IOException e) {
					return false;
				}
			}
		}
		return false;
	}
}
