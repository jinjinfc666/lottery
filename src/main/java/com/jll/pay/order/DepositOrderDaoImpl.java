package com.jll.pay.order;

import java.util.Date;

import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.entity.MoneyInInfo;

@Repository
public class DepositOrderDaoImpl extends HibernateDaoSupport implements DepositOrderDao
{
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory)
	{
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public MoneyInInfo saveDepositOrder(String userName, String rechargeType, float amount, String comment, Date createTime) {
		MoneyInInfo depositOrder = new MoneyInInfo();
		depositOrder.setAmount(amount);
		//depositOrder.setDealTime(dealTime);
		//depositOrder.setOrderNumber(orderNumber);
		depositOrder.setOrderTime(createTime);
		depositOrder.setPayValidatecode(comment);
		depositOrder.setRechargeType(rechargeType);
		depositOrder.setStatus(Constants.DepositOrderState.INIT_OR_PUSHED.getCode());
		depositOrder.setUserID(userName);
		currentSession().save(depositOrder);
		return depositOrder;
	}

	@Override
	public void updateDepositOrder(MoneyInInfo depositOrder) {
		currentSession().merge(depositOrder);
	}

	@Override
	public MoneyInInfo queryDepositOrderById(int orderId) {
		String sql = "from MoneyInInfo where recordID=?";
		MoneyInInfo depositOrder = null;
		
		Query<MoneyInInfo> query = currentSession().createQuery(sql, MoneyInInfo.class);
		query.setParameter(0, orderId);
		try {
			depositOrder = query.getSingleResult();
		}catch(NoResultException ex) {
			
		}
		return depositOrder;
	}
	
  
  
}
