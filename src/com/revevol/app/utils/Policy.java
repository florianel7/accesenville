package com.revevol.app.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Policy {

	// Expiration time of the policy document
    private String expiration;

    // An array of conditions that every upload must satisfy. It must include every field
    // in your HTML form except the GoogleAccessId, signature, file and policy fields.
    private List<String[]> conditions;

    public Policy() {
      conditions = new ArrayList<String[]>();
    }

    /**
     * Sets the expiration time of the policy document to one hour from now.
     * 
     * @return this
     */
    public Policy setExpiration() {
      // Set the policy to be good for an hour.
      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      	calendar.setTime(new Date());
      	calendar.add(Calendar.SECOND, 20);
      Date expirationDate = calendar.getTime();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss'Z'");
      formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
      expiration = formatter.format(expirationDate);
      return this;
    }

    /**
     * Adds an exact match for bucket as a condition to the policy.
     * 
     * @param bucket target bucket for upload
     * @return this
     */
    public Policy setBucket(String bucket) {
      conditions.add(new String[] {"eq", "$bucket", bucket});
      return this;
    }

    /**
     * Adds the name of the object as a condition to the policy.
     * 
     * @param prefix for object name, or blank for any valid characters.
     * @return this
     */
    public Policy setKey(String prefix) {
      conditions.add(new String[] {"starts-with", "$key", prefix});
      return this;
    }

    /**
     * Adds the redirect url
     * 
     * @param redirect url
     * @return this
     */
    public Policy setSuccessActionRedirect(String url) {
    	conditions.add(new String[] {"eq", "$success_action_redirect", url});
    	return this;
    }
    
    /**
     * Adds the success_action_status value
     * 
     * @param success_action_status
     * @return this
     */
    public Policy setSuccessActionStatus(String success_action_status) {
    	conditions.add(new String[] {"eq", "$success_action_status", success_action_status});
    	return this;
    }
  }
