package com.jll.user;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.entity.TbUsers;

@Repository
public class UserDaoImpl extends HibernateDaoSupport implements UserDao
{
  @Autowired
  public void setSuperSessionFactory(SessionFactory sessionFactory)
  {
    super.setSessionFactory(sessionFactory);
  }

	@Override
	public void modifyUserInfo(TbUsers userInfo) {
		currentSession().merge(userInfo);
	}
  
  
}
