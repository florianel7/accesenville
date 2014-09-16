package com.revevol.app.utils;

public class RESTresponse {
	
	private String status;
	
	public RESTresponse(String status){
		this.status=status;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
