package com.jll.entity;

import java.util.Date;

public class MoneyInInfo
{
  private int recordID;
  private String userID;
  private float amount;
  private Date dealTime;
  private int status;
  private String comment;
  private String rechargeType;
  private String payValidatecode;
  private Date orderTime;
  private String orderNumber;
  private float cashAdd;
  
  public int getRecordID()
  {
    return this.recordID;
  }
  
  public void setRecordID(int recordID)
  {
    this.recordID = recordID;
  }
  
  public String getUserID()
  {
    return this.userID;
  }
  
  public void setUserID(String userID)
  {
    this.userID = userID;
  }
  
  public float getAmount()
  {
    return this.amount;
  }
  
  public void setAmount(float amount)
  {
    this.amount = amount;
  }
  
  public Date getDealTime()
  {
    return this.dealTime;
  }
  
  public void setDealTime(Date dealTime)
  {
    this.dealTime = dealTime;
  }
  
  public int getStatus()
  {
    return this.status;
  }
  
  public void setStatus(int status)
  {
    this.status = status;
  }
  
  public String getComment()
  {
    return this.comment;
  }
  
  public void setComment(String comment)
  {
    this.comment = comment;
  }
  
  public String getRechargeType()
  {
    return this.rechargeType;
  }
  
  public void setRechargeType(String rechargeType)
  {
    this.rechargeType = rechargeType;
  }
  
  public String getPayValidatecode()
  {
    return this.payValidatecode;
  }
  
  public void setPayValidatecode(String payValidatecode)
  {
    this.payValidatecode = payValidatecode;
  }
  
  public Date getOrderTime()
  {
    return this.orderTime;
  }
  
  public void setOrderTime(Date orderTime)
  {
    this.orderTime = orderTime;
  }
  
  public String getOrderNumber()
  {
    return this.orderNumber;
  }
  
  public void setOrderNumber(String orderNumber)
  {
    this.orderNumber = orderNumber;
  }
  
  public float getCashAdd()
  {
    return this.cashAdd;
  }
  
  public void setCashAdd(float cashAdd)
  {
    this.cashAdd = cashAdd;
  }
}
