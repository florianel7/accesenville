package com.revevol.app;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;

import com.revevol.app.model.Alert;
import com.revevol.app.model.EntityParent;
import com.revevol.app.model.Metadata;
import com.revevol.app.utils.Methods;
import com.revevol.app.utils.Params;

public class UploadFileServlet extends HttpServlet {

	private static final Logger	log	= Logger.getLogger(UploadFileServlet.class.getName());
	private Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		resp.setContentType("text/plain");
		ObjectifyService.register(EntityParent.class);
		ObjectifyService.register(Alert.class);
		ObjectifyService.register(Metadata.class);

		resp.setContentType("text/html; charset=UTF-8");
		if("GET_FILE_LINK_BY_ALERT".equals(req.getParameter("method"))){
			getFileLinkByAlert(req,resp);
		}
	}

	private void getFileLinkByAlert(HttpServletRequest req,HttpServletResponse resp) throws IOException {
		String fileName = req.getParameter("fileName");
		String trueLink = getImageLink(fileName);
		resp.getWriter().print(trueLink);
	}

	private String getImageLink(String fileName) {
		InputStream privateKeyStream = null;
		try {

			//load private key
			privateKeyStream = new FileInputStream(this.getServletContext().getRealPath(Params.REAL_KEY_PATH));
			KeyStore 	ks = KeyStore.getInstance("PKCS12");
			ks.load(privateKeyStream, "notasecret".toCharArray());
			PrivateKey myOwnKey=(PrivateKey)ks.getKey("privatekey", "notasecret".toCharArray());

			//prepare data to return
			final String HTTP_Verb="GET";
			final long EXPIRATION=((new java.util.Date().getTime()/1000))+(50000000);
			String canonicalized_Resource="/"+Params.BUCKET_NAME+"/"+fileName;

			//write information to sign
			String stringToSign = HTTP_Verb + "\n" +
					"\n"+
					"\n"+
					EXPIRATION + "\n" +
					canonicalized_Resource;

			//sign information
			String stringSigned = Methods.sign(stringToSign, myOwnKey);

			//prepare final URL
			String finalURL= "https://storage.googleapis.com/"+Params.BUCKET_NAME+"/"+fileName+"?GoogleAccessId="+Params.GOOGLE_ACCESS_ID+"&Expires="+EXPIRATION+"&Signature="+URLEncoder.encode(stringSigned,"UTF-8");

			//return final URL
			return finalURL;
		}
		catch(Exception ex){
			return "";
		}
	}

	public String getImageThumb(String fileName, String trueLink)
	{       
		String url;
		String cloudStoragePath = "/gs/"+Params.BUCKET_NAME+"/"+fileName;
		ServingUrlOptions options = ServingUrlOptions.Builder.withGoogleStorageFileName(cloudStoragePath);
		options.secureUrl(true);
		options.crop(true);
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		try {
			url = imagesService.getServingUrl(options);
		} catch (Exception e) {
			url = trueLink;
		}
		return url;
	}
}
