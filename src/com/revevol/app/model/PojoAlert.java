package com.revevol.app.model;

public class PojoAlert {
	private String email;
	private Double lat;
	private Double lgt;
	
	public PojoAlert(){
		
	}
	
	public PojoAlert(String email, Double lat, Double lgt) {
		this.email = email;
		this.lat = lat;
		this.lgt = lgt;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLgt() {
		return lgt;
	}
	public void setLgt(Double lgt) {
		this.lgt = lgt;
	}
	
}
