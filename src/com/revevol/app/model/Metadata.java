package com.revevol.app.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Metadata {
	@Id Long id;
	@Index String key_alert;
	@Index String value;
    @Index String email;
    @Index Long creationDate;

    public Metadata(){
    	
    }

	public Metadata(String key_alert, String value, String email, Long creationDate) {
		super();
		this.key_alert = key_alert;
		this.value = value;
		this.email = email;
		this.creationDate = creationDate;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}

	public String getKey_alert() {
		return key_alert;
	}

	public void setKey_alert(String key_alert) {
		this.key_alert = key_alert;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
    
	
    
}
