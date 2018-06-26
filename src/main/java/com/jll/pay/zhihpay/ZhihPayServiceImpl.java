package com.jll.pay.zhihpay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.http.HttpRemoteStub;
import com.jll.common.utils.RSAUtils;
import com.jll.entity.MoneyInInfo;
import com.jll.entity.TbBankback;
import com.jll.entity.TbUsers;
import com.jll.entity.display.ZhihPayNotices;
import com.jll.pay.PaymentDao;
import com.jll.pay.order.DepositOrderDao;
import com.jll.sys.config.ReceiverBankCardDao;
import com.jll.user.UserDao;

@Configuration
@PropertySource("classpath:zhih-pay.properties")
@Service
@Transactional
public class ZhihPayServiceImpl implements ZhihPayService
{
	private Logger logger = Logger.getLogger(ZhihPayServiceImpl.class);
	/*private final String FAILED_CODE = "1";
	
	private final String SUCCESS_CODE = "0";*/
	
	//private final String FAILED_CODE_ONLINE_PAY = "1";
	
	private final String SUCCESS_CODE = "SUCCESS";
	
	@Resource
	ZhihPayDao tlCloudDao;
	
	@Resource
	PaymentDao payDao;
	
	@Resource
	DepositOrderDao depositOrderDao;
	
	@Resource
	ReceiverBankCardDao recBankCardDao;
	
	@Resource
	UserDao userDao;
		  
	@Value("${api.server}")
	private String apiServer;
	  
	@Value("${api.scan_pay}")
	private String apiScanQRPay;
	  
	@Value("${api.online_bank_pay}")
	private String apiOnLineBankPay;
	  
	@Value("${cons.versionId}")
	private String versionId;
		
	@Value("${cons.versionId.onlinebank}")
	private String onlineBankVersionId;
	
	@Value("${cons.signType}")
	private String signType;
	
	@Value("${api.scan_pay.notify_url}")
	private String scanPayAsynNOtifyURL;
	
	@Value("${api.online_bank_pay.notify_url}")
	private String onlineBankPayAsynNotifyUrl;	
	
	@Value("${merchant.merId}")
	private String merchantMerId;
	
	@Value("${merchant.key}")
	private String merchantKey;
	
	@Value("${merchant.public_key}")
	private String merchantPubKey;
	
	/*
	@Value("${merchant1.payMode}")
	private String merchant1PayMode;
	
	@Value("${merchant1.receivableType}")
	private String merchant1ReceivableType;
	
	
	@Value("${merchant2.merId}")
	private String merchant2MerId;
	
	@Value("${merchant2.key}")
	private String merchant2Key;
	
	@Value("${merchant2.payMode}")
	private String merchant2PayMode;
	
	@Value("${merchant2.receivableType}")
	private String merchant2ReceivableType;
	
	
	
	
	@Value("${merchant3.merId}")
	private String merchant3MerId;
	
	@Value("${merchant3.key}")
	private String merchant3Key;
	
	@Value("${merchant3.payMode}")
	private String merchant3PayMode;
	
	@Value("${merchant3.receivableType}")
	private String merchant3ReceivableType;*/
	
	//private List<Merchant> merchants/* = new ArrayList<>()*/;
	
	@PostConstruct
	public void init() {
		/*merchants = new ArrayList<>();
		
		Merchant merchant = new Merchant();
		merchant.setKey(merchant1Key);
		merchant.setMerId(merchant1MerId);
		List<String> payModes = Arrays.asList(merchant1PayMode.split("\\|"));
		merchant.setPayModes(payModes);
		merchant.setReceivableType(merchant1ReceivableType);
		merchants.add(merchant);
		
		Merchant merchant2 = new Merchant();
		merchant2.setKey(merchant2Key);
		merchant2.setMerId(merchant2MerId);
		List<String> payModes2 = Arrays.asList(merchant2PayMode.split("\\|"));
		merchant2.setPayModes(payModes2);
		merchant2.setReceivableType(merchant2ReceivableType);
		merchants.add(merchant2);
		
		Merchant merchant3 = new Merchant();
		merchant3.setKey(merchant3Key);
		merchant3.setMerId(merchant3MerId);
		List<String> payModes3 = Arrays.asList(merchant3PayMode.split("\\|"));
		merchant3.setPayModes(payModes3);
		merchant3.setReceivableType(merchant3ReceivableType);
		merchants.add(merchant3);*/
	}
	
	@Override
	public boolean isAuthorized(String ip) {
		String roleName = "zhih_pay_api";
		long count = tlCloudDao.queryWhiteListCount(ip, roleName);
		
		return count == 0?false:true;
	}


	@Override
	public String receiveDepositOrder(int orderId) {
		
		return null;
	}


	@Override
	public boolean isOrderExisting(int orderId) {
		long count = tlCloudDao.queryDepositOrderCount(orderId);
		return count == 0?false:true;
	}

	@Override
	public String saveDepositOrder(Map<String, Object> params) {
		
		String userName = (String)params.get("userName");
		String payMode = (String)params.get("rechargeType");
		float amount = (Float)params.get("amount");
		String comment = (String)params.get("comment");
		String payModeDesc = Constants.ZHIH_PAY_MODE.getDescByCode(payMode);
		if(params.get("createTime") == null) {
			params.put("createTime", new Date());
		}
		
		MoneyInInfo depositOrder = depositOrderDao.saveDepositOrder(userName, payModeDesc, amount, comment, (Date)params.get("createTime"));
		
		if(depositOrder == null) {
			throw new RuntimeException();
		}
		
		params.put("depositOrder", depositOrder);
		
		return String.valueOf(Message.status.SUCCESS.getCode());
		
		
		
	}
	
	private boolean isResponseSuccess(Map<String, Object> response) {
		Map<String, String> resMap = new HashMap<>();
		if(response.size() == 0) {
			logger.debug("Can't read response from the zhih-pay server!!!");
			return false;
		}
		
		int status = (int)response.get("status");
		if(status != HttpStatus.SC_OK) {
			return false;
		}else {
			String body = (String)response.get("responseBody");
			
			logger.debug("the response is ::: " + (body == null?"":body));
			
			ByteArrayInputStream bis = null;
			if(body != null && body.length() > 0) {
				try {
					SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
					bis = new ByteArrayInputStream(body.getBytes("utf-8"));
					saxParser.parse(bis, new DefaultHandler() {
						private String key = null;
						private String val = null;
						public void startElement (String uri, String localName,
	                              String qName, Attributes attributes)
					        throws SAXException
					    {
							key = qName;
							val = null;
					    }
						
						public void characters (char ch[], int start, int length)
						        throws SAXException
						{
							if(val != null) {
								val = val + new String(ch, start, length);								
							}else {
								val = new String(ch, start, length);
							}
						}
						
						public void endElement (String uri, String localName, String qName)
						        throws SAXException
						{
							resMap.put(key, val);
						}
					});
					
					if(resMap.size() == 0) {
						logger.debug("can't read the response!!!");
						return false;
					}
					
					String respCode = resMap.get("resp_code");
					if(respCode == null) {
						logger.debug("retCode is null");
						return false;
					}
					
					
					if(respCode.equals(SUCCESS_CODE)) {
						return true;
					}
					
					return false;
				} catch (IOException | ParserConfigurationException | SAXException e) {
					e.printStackTrace();
					return false;
				}finally {
					if(bis != null) {
						try {
							bis.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean isOnlineBankResponseSuccess(Map<String, Object> response) {
		int status = (int)response.get("status");
		if(status != HttpStatus.SC_OK) {
			return false;
		}else {
			String body = (String)response.get("responseBody");
			if(body != null && body.length() > 0) {
				if(body.contains("pay_form")) {
					return true;
				}else {
					return false;
				}
			}
		}
		return false;
	}
	
	private SortedMap<String, Object> produceParamsOfScanQRPay(Map<String, Object> params){
				
		DecimalFormat numFormat = new DecimalFormat("##0.00");
		MoneyInInfo depositOrder = (MoneyInInfo)params.get("depositOrder");
		SortedMap<String, Object> pushParams = new TreeMap<>();
		StringBuffer buffer = new StringBuffer();
		
		pushParams.put("merchant_code", merchantMerId);
		pushParams.put("service_type", (String)params.get("rechargeType"));
		pushParams.put("notify_url", params.get("asynNotifyURL"));
		pushParams.put("interface_version", versionId);
		//pushParams.put("client_ip", params.get("reqIP"));
		pushParams.put("client_ip", "121.96.59.56");
		pushParams.put("sign_type", signType);
		pushParams.put("order_no", String.valueOf(depositOrder.getRecordID()));
		pushParams.put("order_time", params.get("createTime"));
		pushParams.put("order_amount", numFormat.format(params.get("amount")));
		pushParams.put("product_name", "lottery");
		Iterator<String> keys = pushParams.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Object valObj = pushParams.get(key);
			String v = "";
			if(valObj.getClass().getName().equals("java.lang.String")) {
				v = (String)valObj;
			}else if(valObj.getClass().getName().equals("java.lang.Integer")) {
				v = String.valueOf(((Integer)valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Float")) {
				v = String.valueOf(((Float) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Long")) {
				v = String.valueOf(((Long) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Boolean")) {
				v = ((Boolean) valObj).toString();
			}
			if (StringUtils.isNotEmpty(v) && !"sign".equals(key) && !"sign_type".equals(key)) {
				buffer.append(key + "=" + v + "&");
			}
		}
		
		if(buffer.toString().endsWith("&")) {
			buffer.deleteCharAt(buffer.length()-1);
		}
		String sign = null;
		try {
			sign = RSAUtils.signByPrivateKey(buffer.toString(), merchantKey);
		} catch (Exception e) {
			
		}
		if(sign == null || sign.length() == 0) {
			return null;
		}
		
		pushParams.put("sign", sign);
		return pushParams;
	}

	private SortedMap<String, Object> produceParamsOfOnlinePay(Map<String, Object> params){
		DecimalFormat numFormat = new DecimalFormat("##0.00");
		MoneyInInfo depositOrder = (MoneyInInfo)params.get("depositOrder");
		SortedMap<String, Object> pushParams = new TreeMap<>();
		StringBuffer buffer = new StringBuffer();
		
		pushParams.put("merchant_code", merchantMerId);
		pushParams.put("service_type", (String)params.get("rechargeType"));
		pushParams.put("notify_url", params.get("asynNotifyURL"));
		//pushParams.put("notify_url", "zhmyb.top");
		pushParams.put("interface_version", onlineBankVersionId);
		//pushParams.put("client_ip", params.get("reqIP"));
		//pushParams.put("client_ip", "121.96.59.56");
		//pushParams.put("client_ip_check", 0);
		pushParams.put("sign_type", signType);
		pushParams.put("order_no", String.valueOf(depositOrder.getRecordID()));
		pushParams.put("order_time", params.get("createTime"));
		pushParams.put("order_amount", numFormat.format(params.get("amount")));
		pushParams.put("product_name", "lottery");
		pushParams.put("redo_flag", 1);
		pushParams.put("return_url", "http://jll2019.com");
		pushParams.put("input_charset", "UTF-8");
		Iterator<String> keys = pushParams.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Object valObj = pushParams.get(key);
			String v = "";
			if(valObj == null) {
				continue;
			}
			if(valObj.getClass().getName().equals("java.lang.String")) {
				v = (String)valObj;
			}else if(valObj.getClass().getName().equals("java.lang.Integer")) {
				v = String.valueOf(((Integer)valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Float")) {
				v = String.valueOf(((Float) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Long")) {
				v = String.valueOf(((Long) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Boolean")) {
				v = ((Boolean) valObj).toString();
			}
			if (StringUtils.isNotEmpty(v) && !"sign".equals(key) && !"sign_type".equals(key)) {
				buffer.append(key + "=" + v + "&");
			}
		}
		
		if(buffer.toString().endsWith("&")) {
			buffer.deleteCharAt(buffer.length()-1);
		}
		String sign = null;
		try {
			sign = RSAUtils.signByPrivateKey(buffer.toString(), merchantKey);
		} catch (Exception e) {
			
		}
		if(sign == null || sign.length() == 0) {
			return null;
		}
		
		logger.debug("the signature items:" + buffer.toString() +"   signature::" + sign + "  length of signature:" + sign.length());
		
		pushParams.put("sign", sign);
		return pushParams;
	}
	
	@Override
	public String processScanPay(Map<String, Object> params) {
		
		Date createTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		URI url = null;
		boolean isSuccess = true;
		SortedMap<String, Object> pushParams = new TreeMap<>();
		Map<String, String> reqHeaders = new HashMap<>();
		
		params.put("createTime", createTime);
		String ret = saveDepositOrder(params);
		if(!ret.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
			return ret;
		}
		
		MoneyInInfo depositOrder = (MoneyInInfo)params.get("depositOrder");
		reqHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		//reqHeaders.put("Content-Type", "application/json");
		params.put("createTime", format.format(createTime));
		String reqHost = (String)params.get("reqHost");
		String reqContext = (String)params.get("reqContext");
		params.put("asynNotifyURL", scanPayAsynNOtifyURL.replace("{host}", reqHost).replace("{context}", reqContext));
		
		pushParams = produceParamsOfScanQRPay(params);
		if(pushParams == null || pushParams.size() == 0) {
			return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_SIGNATURE_PARAMS.getCode();
		}
		 try {
		 	url = new URI(apiServer + apiScanQRPay);
		 } catch (URISyntaxException e) {
		 	return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_CANCEL_ORDER.getCode();
		 }

		 Map<String, Object> response = HttpRemoteStub.synPost(url, reqHeaders, pushParams);
		 isSuccess = isResponseSuccess(response);
		 logger.debug("If request successful::::" + isSuccess);
		 depositOrder.setOrderNumber(String.valueOf(depositOrder.getRecordID()));
		 if(isSuccess) {
			 depositOrderDao.updateDepositOrder(depositOrder);
			 String qrcode = null;
			 String qrCodeKey = "qrcode";
			 String jsonStr = (String)response.get("responseBody");
			 qrcode = readQrcode(qrCodeKey, jsonStr);
			 
			 params.put("qrcode", qrcode);
		 	return String.valueOf(Message.status.SUCCESS.getCode());
		 }else {
			 //failed to push 
			 depositOrder.setStatus(Constants.DepositOrderState.FAILED_PUSH.getCode());
			 depositOrderDao.updateDepositOrder(depositOrder);
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER.getCode();
		 }
	}


	private String readQrcode(String qrCodeKey, String xmlSeg) {
		Map<String, String> resMap = new HashMap<>();
						
		ByteArrayInputStream bis = null;
		if(xmlSeg != null && xmlSeg.length() > 0) {
			try {
				SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
				bis = new ByteArrayInputStream(xmlSeg.getBytes("utf-8"));
				saxParser.parse(bis, new DefaultHandler() {
					private String key = null;
					private String val = null;
					public void startElement (String uri, String localName,
                              String qName, Attributes attributes)
				        throws SAXException
				    {
						key = qName;
						val = null;
				    }
					
					public void characters (char ch[], int start, int length)
					        throws SAXException
					{
						if(val != null) {
							val = val + new String(ch, start, length);								
						}else {
							val = new String(ch, start, length);
						}
					}
					
					public void endElement (String uri, String localName, String qName)
					        throws SAXException
					{
						resMap.put(key, val);
					}
				});
				
				if(resMap.size() == 0) {
					logger.debug("can't read the response!!!");
					return null;
				}
				
				String qrCode = resMap.get(qrCodeKey);
				
				return qrCode;
			} catch (IOException | ParserConfigurationException | SAXException e) {
				e.printStackTrace();
				return null;
			}finally {
				if(bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
					}
				}
			}			
		}
		return null;	
		
	}


	@Override
	public String processOnlineBankPay(Map<String, Object> params) {
		Date createTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//URI url = null;
		boolean isSuccess = true;
		SortedMap<String, Object> pushParams = new TreeMap<>();
		Map<String, String> reqHeaders = new HashMap<>();
		
		params.put("createTime", createTime);
		String ret = this.saveDepositOrder(params);
		if(!ret.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
			return ret;
		}
		
		MoneyInInfo depositOrder = (MoneyInInfo)params.get("depositOrder");
		reqHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		params.put("createTime", format.format(createTime));
		String reqHost = (String)params.get("reqHost");
		String reqContext = (String)params.get("reqContext");
		params.put("asynNotifyURL", onlineBankPayAsynNotifyUrl.replace("{host}", reqHost).replace("{context}", reqContext));
		
		pushParams = produceParamsOfOnlinePay(params);
		if(pushParams == null || pushParams.size() == 0) {
			return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_SIGNATURE_PARAMS.getCode();
		}
		
		params.clear();
		Iterator<String> ite = pushParams.keySet().iterator();
		while(ite.hasNext()) {
			String key = ite.next();
			Object val = pushParams.get(key);
			
			params.put(key, val);
		}
		
		 /*try {
		 	url = new URI(apiOnLineBankPay);
		 } catch (URISyntaxException e) {
		 	return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_CANCEL_ORDER.getCode();
		 }

		 Map<String, Object> response = HttpRemoteStub.synPost(url, reqHeaders, pushParams);
		 
		 logger.debug("response from server :: " + (response.get("responseBody") == null ?"":(String)response.get("responseBody")));
		 isSuccess = isOnlineBankResponseSuccess(response);
		 
		 String redirect = (String)response.get("responseBody");
		 
		 params.put("redirect", redirect);*/
		 
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
	public String receiveNoties(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isValid(Map<String, Object> params) {
		String payMode = (String)params.get("rechargeType");
		if("00023".equals(payMode)) {
			return verifyQuickPayment(params);
		}
		return true;
	}
	
	private boolean verifyQuickPayment(Map<String, Object> params) {
		String bankCardNo = (String)params.get("bankCardNo");
		String userName = (String)params.get("userName");
		String phone = (String)params.get("phone");
		String idNo = (String)params.get("idNo");
		String expireDate = (String)params.get("expireDate");
		String CVn2 = (String)params.get("CVn2");
		if(bankCardNo == null || bankCardNo.length() == 0
				|| userName == null || userName.length() == 0
				|| phone == null || phone.length() == 0
				|| CVn2 == null || CVn2.length() == 0
				|| idNo == null || idNo.length() == 0
				|| expireDate == null || expireDate.length() == 0) {
			return false;
		}
		return true;
	}


	@Override
	public boolean isNoticesValid(ZhihPayNotices notices, int noticeType) {
		String sign = null;
		SortedMap<String, Object> pushParams = new TreeMap<>();
		StringBuffer buffer = new StringBuffer();
		pushParams.put("bank_seq_no", notices.getBank_seq_no());
		pushParams.put("extra_return_param", notices.getExtra_return_param());
		pushParams.put("interface_version", notices.getInterface_version());
		pushParams.put("merchant_code", notices.getMerchant_code());
		pushParams.put("notify_id", notices.getNotify_id());
		pushParams.put("notify_type", notices.getNotify_type());
		pushParams.put("order_amount", notices.getOrder_amount());
		pushParams.put("order_no", notices.getOrder_no());
		pushParams.put("order_time", notices.getOrder_time());
		pushParams.put("orginal_money", notices.getOrginal_money());
		pushParams.put("trade_no", notices.getTrade_no());
		pushParams.put("trade_status", notices.getTrade_status());
		pushParams.put("trade_time", notices.getTrade_time());
		
		Iterator<String> keys = pushParams.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Object valObj = pushParams.get(key);
			String v = "";
			if(valObj == null) {
				continue;
			}
			if(valObj.getClass().getName().equals("java.lang.String")) {
				v = (String)valObj;
			}else if(valObj.getClass().getName().equals("java.lang.Integer")) {
				v = String.valueOf(((Integer)valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Float")) {
				v = String.valueOf(((Float) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Long")) {
				v = String.valueOf(((Long) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Boolean")) {
				v = ((Boolean) valObj).toString();
			}
			if (StringUtils.isNotEmpty(v) && !"sign".equals(key) && !"sign_type".equals(key)) {
				buffer.append(key + "=" + v + "&");
			}
		}
		
		if(buffer.toString().endsWith("&")) {
			buffer.deleteCharAt(buffer.length()-1);
		}
		
		try {
			return RSAUtils.validateSignByPublicKey(buffer.toString(), merchantPubKey, notices.getSign());
		} catch (Exception e) {
			return false;
		}	
		
	}


	@Override
	public String receiveDepositOrder(int orderId, float amount) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeNum = format.format(new Date());
		
		MoneyInInfo depositOrder = depositOrderDao.queryDepositOrderById(orderId);
		TbUsers user  = payDao.queryUserInfo(depositOrder.getUserID());
		if(user == null) {
			return Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode();
		}
		
		BigDecimal userMoney = new BigDecimal(user.getUserMoney());
		BigDecimal allMoney = new BigDecimal(user.getAllMoney());
		BigDecimal inAmount = new BigDecimal(amount);
		
		TbBankback depositRecord = new TbBankback();
		depositRecord.setActivityType(0);
		depositRecord.setAddTime(new Date());
		depositRecord.setBackAfter(userMoney.add(inAmount).floatValue());
		depositRecord.setBackBefor(user.getUserMoney());
		depositRecord.setBackMoney(amount);
		depositRecord.setBackType("存款");
		depositRecord.setBackTypeText("系统充值");
		//depositRecord.setBankUser(bankUser);
		depositRecord.setFollows(depositOrder.getRechargeType()+ "充值成功");
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
	public boolean isOrderSuccess(ZhihPayNotices notices) {
		String status = notices.getTrade_status();
		return "SUCCESS".equals(status);
	}

	@Override
	public boolean isOrderNotified(ZhihPayNotices notices) {
		String orderId = notices.getOrder_no();
		MoneyInInfo depositOrder = depositOrderDao.queryDepositOrderById(Integer.parseInt(orderId));
		
		if(depositOrder.getStatus() == 1) {
			return true;
		}
		
		return false;
	}
}
