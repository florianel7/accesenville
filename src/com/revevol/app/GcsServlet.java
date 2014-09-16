package com.revevol.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;

import com.revevol.app.model.POSTobjData;
import com.revevol.app.utils.Methods;
import com.revevol.app.utils.Params;
import com.revevol.app.utils.Policy;

public class GcsServlet extends HttpServlet {

	private static final long	serialVersionUID	= -2897184013542686122L;
	private static final Logger log = Logger.getLogger(GcsServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		// execute post method
		this.doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			InputStream privateKeyStream = null;
			try {
				String realPath = this.getServletContext().getRealPath(Params.REAL_KEY_PATH);
				//load private key
				privateKeyStream = new FileInputStream(this.getServletContext().getRealPath(Params.REAL_KEY_PATH));
				KeyStore ks = KeyStore.getInstance("PKCS12");
				ks.load(privateKeyStream, "notasecret".toCharArray());
				PrivateKey myOwnKey = (PrivateKey) ks.getKey("privatekey", "notasecret".toCharArray());

				//generate policy
				Policy 	policy = new Policy();
				policy.setExpiration().setBucket(Params.BUCKET_NAME).setKey("").setSuccessActionStatus("201");

				//sign the policy
				String policyJsonStr = new Gson().toJson(policy);
				String policyToSign = Base64.encodeBase64String(policyJsonStr.getBytes());
				String signature = Methods.sign(policyToSign, myOwnKey);

				//prepare data to return
				POSTobjData respToRet = new POSTobjData();
				respToRet.setMethod("POST");
				respToRet.setUrl("https://" + Params.BUCKET_NAME + "." + Params.GOOGLE_STORAGE_API);
				respToRet.setBucket(Params.BUCKET_NAME);
				respToRet.setPolicy(policyToSign);
				respToRet.setSignature(signature);
				respToRet.setGoogleAccessID(Params.GOOGLE_ACCESS_ID);
				respToRet.setRandomName(createRandomName());

				//convert data into JSON 
				String response = new Gson().toJson(respToRet);

				//return data
				resp.setContentType("application/json");
				resp.getWriter().print(response);
			}
			finally {
				if (privateKeyStream != null) {
					privateKeyStream.close();
				}
			}
		}
		catch (Exception ex) {
			log.warning("An error occured: " + Methods.printStackStrace(ex));
			try {
				resp.getWriter().print("ERROR");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * For every image we create a random name formed by timestamp + random number
	 * @return
	 */
	private String createRandomName() {
		Date dateNow = new Date();
		String dateNowString = new Long(dateNow.getTime()).toString();
		int intValue = (int) Math.floor((Math.random()*100000)+1);
		String randomString = new Integer(intValue).toString();
		return dateNowString+"_"+randomString;
	}
}
