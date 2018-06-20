package com.jll.entity;

import java.util.Date;

public class TbBankback
{
  private int id;
  private String tradeNum;
  private String userName;
  private String backType;
  private String backTypeText;
  private float backMoney;
  private float backBefor;
  private float backAfter;
  private String state;
  private String follows;
  private Date addTime;
  private String webSite;
  private Date updateTime;
  private String bankUser;
  private int activityType;
  private int ifDeal;
  private int ifAutoTransfer;
  
  public int getId()
  {
    return this.id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }
  
  public String getTradeNum()
  {
    return this.tradeNum;
  }
  
  public void setTradeNum(String tradeNum)
  {
    this.tradeNum = tradeNum;
  }
  
  public String getUserName()
  {
    return this.userName;
  }
  
  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  public String getBackType()
  {
    return this.backType;
  }
  
  public void setBackType(String backType)
  {
    this.backType = backType;
  }
  
  public String getBackTypeText()
  {
    return this.backTypeText;
  }
  
  public void setBackTypeText(String backTypeText)
  {
    this.backTypeText = backTypeText;
  }
  
  public float getBackMoney()
  {
    return this.backMoney;
  }
  
  public void setBackMoney(float backMoney)
  {
    this.backMoney = backMoney;
  }
  
  public float getBackBefor()
  {
    return this.backBefor;
  }
  
  public void setBackBefor(float backBefor)
  {
    this.backBefor = backBefor;
  }
  
  public float getBackAfter()
  {
    return this.backAfter;
  }
  
  public void setBackAfter(float backAfter)
  {
    this.backAfter = backAfter;
  }
  
  public String getState()
  {
    return this.state;
  }
  
  public void setState(String state)
  {
    this.state = state;
  }
  
  public String getFollows()
  {
    return this.follows;
  }
  
  public void setFollows(String follows)
  {
    this.follows = follows;
  }
  
  public Date getAddTime()
  {
    return this.addTime;
  }
  
  public void setAddTime(Date addTime)
  {
    this.addTime = addTime;
  }
  
  public String getWebSite()
  {
    return this.webSite;
  }
  
  public void setWebSite(String webSite)
  {
    this.webSite = webSite;
  }
  
  public Date getUpdateTime()
  {
    return this.updateTime;
  }
  
  public void setUpdateTime(Date updateTime)
  {
    this.updateTime = updateTime;
  }
  
  public String getBankUser()
  {
    return this.bankUser;
  }
  
  public void setBankUser(String bankUser)
  {
    this.bankUser = bankUser;
  }
  
  public int getActivityType()
  {
    return this.activityType;
  }
  
  public void setActivityType(int activityType)
  {
    this.activityType = activityType;
  }
  
  public int getIfDeal()
  {
    return this.ifDeal;
  }
  
  public void setIfDeal(int ifDeal)
  {
    this.ifDeal = ifDeal;
  }
  
  public int getIfAutoTransfer()
  {
    return this.ifAutoTransfer;
  }
  
  public void setIfAutoTransfer(int ifAutoTransfer)
  {
    this.ifAutoTransfer = ifAutoTransfer;
  }
}
