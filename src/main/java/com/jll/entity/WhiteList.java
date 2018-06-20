package com.jll.entity;

import java.util.Date;

public class WhiteList
{
  private int id;
  
  private String roleName;
  
  private float roleDes;
  
  private String ip;
  
  private Date createTime;
  
  private String creater ;

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getRoleName() {
	return roleName;
}

public void setRoleName(String roleName) {
	this.roleName = roleName;
}

public float getRoleDes() {
	return roleDes;
}

public void setRoleDes(float roleDes) {
	this.roleDes = roleDes;
}

public String getIp() {
	return ip;
}

public void setIp(String ip) {
	this.ip = ip;
}

public Date getCreateTime() {
	return createTime;
}

public void setCreateTime(Date createTime) {
	this.createTime = createTime;
}

public String getCreater() {
	return creater;
}

public void setCreater(String creater) {
	this.creater = creater;
}
  
  
}
