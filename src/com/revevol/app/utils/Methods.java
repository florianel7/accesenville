package com.revevol.app.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Methods {
	
	/**
	   * Signs the input data with the private key.
	   * 
	   * @param  input data 
	   * @return data signed
	   * @throws NoSuchAlgorithmException
	   * @throws InvalidKeyException
	   * @throws SignatureException
	   * @throws UnsupportedEncodingException
	   */
	  public static String sign(String policy, PrivateKey key) throws NoSuchAlgorithmException,InvalidKeyException, SignatureException, UnsupportedEncodingException {
	    Signature signer = Signature.getInstance("SHA256withRSA");
	    signer.initSign(key);
	    signer.update(policy.getBytes("UTF-8"));
	    return new String(org.apache.commons.codec.binary.Base64.encodeBase64(signer.sign(), false), "UTF-8");
	  }

	/**
	 * Calculate the delay for each comment
	 * @param result
	 */
//	public static void calculateDelayAttributeInEveryComment()(JSONArray result){
//		if(result != null
//				&& result.length() > 0){
//			Date dateNow = new Date();
//			for (int i = 0; i < result.length(); i++) {
//				try {
////					JSONObject obj = result.getJSONObject(i);
////					if(obj.has("DEADLINE")){
////						long deadline =  new Long((String)obj.get("DEADLINE"));
////						int delay = (int) Math.round( (dateNow.getTime() - (deadline*1000)) / ( 1000 * 60 * 60 * 24) ); // 1000 * 60 * 60 * 24
////						//transform the delay in positive
////
////						obj.put("DELAY", delay);
////					}
//
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	/**
	 * transform a string object in a not null string 
	 * (empty string like "" instead of null)
	 * @param input
	 * @return
	 */
	public static String getNotNullString(String input){
		if(input == null
				|| input.trim() == ""
				|| input.trim() == "null"
				|| input.trim().equals("undefined")){
			return "";
		}
		return input;
	}


	/**
	 * Print the stack trace of an exception
	 * 
	 * @param e
	 *            Exception for which you want to print out the stack trace
	 * @return stack trace that you want to print out
	 */
	public static String printStackStrace(Exception e) {
		StringBuilder exceptionStackTrace = new StringBuilder();
		for (int i = 0; i < e.getStackTrace().length; i++) {
			exceptionStackTrace.append("\t" + e.getStackTrace()[i]);
			exceptionStackTrace.append("\n");
		}
		return exceptionStackTrace.toString();
	}

	/**
	 * Get current date in Google API format (EEE, dd MMM yyyy HH:mm:ss zzz)
	 * @return String that represent date in following format EEE, dd MMM yyyy HH:mm:ss zzz
	 */
	public static String getCurrentDate(){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTime(new Date());
		Date currentDate = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.format(currentDate);
	}

	/**
	 * Method to delete all the accents
	 * @param str
	 * @return
	 */
	public static String deAccent(String str) {
		String tmp = str;
		tmp = tmp.replaceAll("[à]", "a'");
		tmp = tmp.replaceAll("[�?è]", "e'");
		tmp = tmp.replaceAll("[é]", "e'");
		tmp = tmp.replaceAll("[ì]", "i'");
		tmp = tmp.replaceAll("[ò]", "o'");
		tmp = tmp.replaceAll("[ù�?]", "u'");
		String tmp2 = "";
		for (int i = 0; i < tmp.length(); i++) {
			if ((int) tmp.charAt(i) <= 256)
				tmp2 += tmp.charAt(i);
			else
				tmp2 += " ";
			if ((int) tmp.charAt(i) == 92) {
				tmp2 += " ";
			}
		}

		return Normalizer.normalize(tmp2, Normalizer.Form.NFD).replaceAll("\\p{IsM}+", "");

	}
}
