package com.jll.pay;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl
  implements PaymentService
{
  @Resource
  PaymentDao trendAnalysisDao;
  
  public long queryDepositTimes(String userName)
  {
    return this.trendAnalysisDao.queryDepositTimes(userName);
  }
}
