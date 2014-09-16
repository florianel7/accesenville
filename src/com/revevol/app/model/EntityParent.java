package com.revevol.app.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class EntityParent {
	@Id 
    Long id;

	public EntityParent() {
	}

	public Long getId() {
		return id;
	}
}
