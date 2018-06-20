package com.jll.pay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jll.entity.TbLtInfo;

@RestController
@RequestMapping({"/payment"})
public class PaymentController
{
  private Logger logger = Logger.getLogger(PaymentController.class);
  @Resource
  PaymentService paymentService;
  
  @RequestMapping(value={"/depositTimes/{userName}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET}, produces={"application/json"})
  public Map<String, Object> depositTimes(@PathVariable("userName") String userName)
  {
    Map<String, Object> ret = new HashMap();
    List<TbLtInfo> recs = null;
    if (StringUtils.isBlank(userName))
    {
      recs = new ArrayList();
      ret.put("status", Integer.valueOf(0));
      ret.put("errorCode", "0001");
      ret.put("errorMes", "Invalid Parameters!");
    }
    else
    {
      long depositTimes = this.paymentService.queryDepositTimes(userName);
      ret.put("status", Integer.valueOf(1));
      ret.put("depositTimes", Long.valueOf(depositTimes));
    }
    return ret;
  }
}
