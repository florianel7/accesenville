package com.revevol.app.model;

public class POSTobjData{
	 private String method;
	 private String url;
	 private String bucket;
	 private String policy;
	 private String signature;
	 private String googleAccessID;
	 private String randomName;
	
	 
	 public POSTobjData(){
		 ;
	 }
	 
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the bucket
	 */
	public String getBucket() {
		return bucket;
	}

	/**
	 * @param bucket the bucket to set
	 */
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	/**
	 * @return the policy
	 */
	public String getPolicy() {
		return policy;
	}

	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(String policy) {
		this.policy = policy;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * @return the googleAccessID
	 */
	public String getGoogleAccessID() {
		return googleAccessID;
	}

	/**
	 * @param googleAccessID the googleAccessID to set
	 */
	public void setGoogleAccessID(String googleAccessID) {
		this.googleAccessID = googleAccessID;
	}

	public String getRandomName() {
		return randomName;
	}

	public void setRandomName(String randomName) {
		this.randomName = randomName;
	}



	
}
