package com.revevol.app.model;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Authorization {
	@Id String token;
	
	@Index String email;
    Date creationTime;
    Date expiredTime;
    
    public Authorization(){
    	
    }
    
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	public Date getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}
    
    
}




//CREATE TABLE GE_CAPITAL.AUTHORIZATION (
//		  ID INT NOT NULL AUTO_INCREMENT,
//		  EMAIL VARCHAR(255),
//		  TOKEN VARCHAR(255),
//		  CREATION_TIME TIMESTAMP NULL,
//		  EXP_TIME TIMESTAMP NULL,
//		  PRIMARY KEY(ID)
//		);