package com.jll.entity;

import java.util.Date;

public class TbLtInfo
{
  private Long id;
  private String lotteryType;
  private String expect;
  private String codes;
  private Date kjTime;
  private Date addTime;
  private String state;
  private int ifCatch;
  
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public String getLotteryType()
  {
    return this.lotteryType;
  }
  
  public void setLotteryType(String lotteryType)
  {
    this.lotteryType = lotteryType;
  }
  
  public String getExpect()
  {
    return this.expect;
  }
  
  public void setExpect(String expect)
  {
    this.expect = expect;
  }
  
  public String getCodes()
  {
    return this.codes;
  }
  
  public void setCodes(String codes)
  {
    this.codes = codes;
  }
  
  public Date getKjTime()
  {
    return this.kjTime;
  }
  
  public void setKjTime(Date kjTime)
  {
    this.kjTime = kjTime;
  }
  
  public Date getAddTime()
  {
    return this.addTime;
  }
  
  public void setAddTime(Date addTime)
  {
    this.addTime = addTime;
  }
  
  public String getState()
  {
    return this.state;
  }
  
  public void setState(String state)
  {
    this.state = state;
  }
  
  public int getIfCatch()
  {
    return this.ifCatch;
  }
  
  public void setIfCatch(int ifCatch)
  {
    this.ifCatch = ifCatch;
  }
}
