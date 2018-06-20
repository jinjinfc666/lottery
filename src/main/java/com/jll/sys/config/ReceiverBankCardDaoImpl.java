package com.jll.sys.config;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.entity.ReceiverBankCard;
import com.jll.entity.TbBankback;

@Repository
public class ReceiverBankCardDaoImpl extends HibernateDaoSupport implements ReceiverBankCardDao
{
  @Autowired
  public void setSuperSessionFactory(SessionFactory sessionFactory)
  {
    super.setSessionFactory(sessionFactory);
  }

	@Override
	public ReceiverBankCard queryReceiverBankCardByBank(String bankCode) {
		//the bankUrl = 99 it means that the bank account is a online bank account
	    // remark is the bank code for online bank account
		String sql = "from ReceiverBankCard where state='启用' and bankUrl='99' and remark=?";
		Query<ReceiverBankCard> query = getSessionFactory().getCurrentSession().createQuery(sql, ReceiverBankCard.class);
		query.setParameter(0, bankCode);
		ReceiverBankCard card = null;
		try {			
			card = query.getSingleResult();
		}catch(NoResultException ex) {
			
		}
		
		return card;
	}

	@Override
	public void addDepositRecord(TbBankback record) {
		currentSession().save(record);
	}

	@Override
	public List<ReceiverBankCard> queryReceiverBankCard() {
		String sql = "from ReceiverBankCard where state='启用' and bankUrl='99' order by id";
		Query<ReceiverBankCard> query = getSessionFactory().getCurrentSession().createQuery(sql, ReceiverBankCard.class);
		List<ReceiverBankCard> cards = new ArrayList<>();
		try {			
			cards = query.list();
		}catch(NoResultException ex) {
			
		}
		
		return cards;
	}
  
  
}
