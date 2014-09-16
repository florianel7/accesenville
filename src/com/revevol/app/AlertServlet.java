package com.revevol.app;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.revevol.app.model.Alert;
import com.revevol.app.model.EntityParent;
import com.revevol.app.model.Message;
import com.revevol.app.model.Metadata;
import com.revevol.app.model.User;
import com.revevol.app.utils.Params;

public class AlertServlet extends HttpServlet {

	private static final Logger	log	= Logger.getLogger(AlertServlet.class.getName());
	private Gson gson = new Gson();
	final Geocoder geocoder = new Geocoder();
	int count = 0;


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		ObjectifyService.register(EntityParent.class);
		ObjectifyService.register(Alert.class);
		ObjectifyService.register(Metadata.class);
		ObjectifyService.register(User.class);
		ObjectifyService.register(Message.class);
		count = 0;
		resp.setContentType("text/html; charset=UTF-8");
		if("NEW_ALERT".equals(req.getParameter("method"))){
			createAlert(req,resp);
		}
		else if("LIST_ALERT".equals(req.getParameter("method"))){
			getListAlert(req,resp);
		}
		else if("EDIT_ALERT".equals(req.getParameter("method"))){
			editAlert(req,resp);
		}
		else if("EDIT_ALERT_ADDRESS".equals(req.getParameter("method"))){
			editAlertAddress(req,resp);
		}
		else if("GET_ALERT".equals(req.getParameter("method"))){
			getAlert(req,resp);
		}
		else if("SEND_EMAIL".equals(req.getParameter("method"))){
			sendEmail(req,resp);
		}
		else if("GET_MESSAGES_BY_ALERT".equals(req.getParameter("method"))){
			getMessagesByAlert(req,resp);
		}
	}

	private void editAlertAddress(HttpServletRequest req,
			HttpServletResponse resp) {
		try {
			if(count < 30){
				Alert newAlert = gson.fromJson(req.getParameter("newAlert"), Alert.class);
				GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(newAlert.getAddress()).setLanguage("en").getGeocoderRequest();
				GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
				if(geocoderResponse != null
						&& "OK".equals(geocoderResponse.getStatus().name())
						&& geocoderResponse.getResults() != null
						&& geocoderResponse.getResults().size() > 0 ){
					newAlert.setLatitude(geocoderResponse.getResults().get(0).getGeometry().getLocation().getLat().doubleValue());
					newAlert.setLongitude(geocoderResponse.getResults().get(0).getGeometry().getLocation().getLng().doubleValue());
					ofy().save().entity(newAlert).now();
					resp.getWriter().print("OK");
				}
				else{
					count++;
					log.severe(geocoderResponse.getStatus().name());
					log.severe("TRY AGAIN... "+ count );
					editAlertAddress(req, resp);
				}
			}
		} catch (Exception e) {
			try {
				resp.getWriter().print("ERROR");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void getMessagesByAlert(HttpServletRequest req,HttpServletResponse resp) throws IOException {
		Long alertId = new Long(req.getParameter("alertId"));
		List<Message> messageList = ofy().load().type(Message.class).filter("alertId", alertId).list();
		resp.getWriter().print(gson.toJson(messageList));
	}

	private void sendEmail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html; charset=UTF-8");
		String message = req.getParameter("message");
		String client = req.getParameter("client");
		boolean sendEmail = new Boolean(req.getParameter("sendEmail"));
		Long alertId = new Long(req.getParameter("alertId"));

		Message mex = new Message();
		mex.setAlertId(alertId);
		mex.setMessage(message);
		mex.setClientEmail(client);
		mex.setSendEmail(sendEmail);

		ofy().save().entity(mex).now();

		if(sendEmail){
			sendEmail(mex);
		}

		resp.getWriter().print("OK");
	}

	private void sendEmail(Message mex) throws UnsupportedEncodingException {
		@SuppressWarnings("deprecation")
		String messageBody = URLEncoder.encode(
				mex.getMessage()+
				"<br><br>" +
				"***E-mail automatique, ne pas repondre***", "UTF-8");
		String subject = "["+Params.EMAIL_NAME+"] Un administrateur a repondu a votre alerte.";
		try {
			URL url = new URL(Params.PROJECT_URL_BASE+"/SendMailServlet");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write("mailTo=" + mex.getClientEmail());
			writer.write("&subject=" + subject);
			writer.write("&messageBody=" + messageBody);
			writer.close();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.severe("Problem occurred sending email");
			}
		} catch (MalformedURLException e) {
			log.severe("Problem occurred sending email");
		} catch (IOException e) {
			log.severe("Problem occurred sending email");
		}
	}

	private void getAlert(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		long alertId = Long.parseLong(req.getParameter("alertId"));
		Alert alert = ofy().load().type(Alert.class).id(alertId).now();
		resp.getWriter().print(gson.toJson(alert));
	}

	private void editAlert(HttpServletRequest req, HttpServletResponse resp) {
		try {
			Alert newAlert = gson.fromJson(req.getParameter("newAlert"), Alert.class);
			ofy().save().entity(newAlert).now();
			resp.getWriter().print("OK");
		} catch (Exception e) {
			try {
				resp.getWriter().print("ERROR");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void getListAlert(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String userJson = req.getParameter("user");
		if(userJson != null
				&& !userJson.equals("undefined")){
			User user = gson.fromJson(req.getParameter("user"),User.class);
			if(user != null
					&& user.getType().equals("Admin")){
				List<Alert> alertList = ofy().load().type(Alert.class).filter("active", true).order("-creationDate").list();
				resp.getWriter().print(gson.toJson(alertList));
			}
			else{
				/**
				 * active by the confirmation by the user
				 * enabled by the administrator
				 */
				List<Alert> alertList = ofy().load().type(Alert.class).filter("active", true).filter("enabled", true).order("-creationDate").list();
				resp.getWriter().print(gson.toJson(alertList));
			}
		}
		else{
			/**
			 * active by the confirmation by the user
			 * enabled by the administrator
			 */
			List<Alert> alertList = ofy().load().type(Alert.class).filter("active", true).filter("enabled", true).order("-creationDate").list();
			resp.getWriter().print(gson.toJson(alertList));
		}
	}

	private void createAnonymousUser(HttpServletRequest req,HttpServletResponse resp,String email) throws UnsupportedEncodingException{
		Random rdm = new Random();
		String rdmString  = new Integer(rdm.nextInt(999999999)).toString();
		User newFastUser = new User();
		newFastUser.setEmail(email);
		newFastUser.setName(email);
		newFastUser.setConfirmationCode(rdmString);
		newFastUser.setPassword(rdmString);
		newFastUser.setType("Default");
		newFastUser.setActive(false);
		ofy().save().entity(newFastUser).now();

		//send confirmation email
		adviseForNewUser(req , resp , rdmString, email);
	}

	private void createAlert(HttpServletRequest req,HttpServletResponse resp) throws IOException {
		Alert newAlert = gson.fromJson(req.getParameter("newAlert"), Alert.class);

		//control if the user exists
		String email = newAlert.getCreationUser();
		User user = ofy().load().type(User.class).id(email).now();
		if(user == null){
			createAnonymousUser(req , resp , email);
			resp.getWriter().print("OK_BUT_CONFIRM_EMAIL");
		}
		else{
			newAlert.setActive(true);
			resp.getWriter().print("OK");
		}

		Key<Alert> alertKey = ofy().save().entity(newAlert).now();
		
		//now send the email to advise for the new alert just arrived
		sendEmailToAdviseForTheNewAlert(newAlert,alertKey);

	}

	private void sendEmailToAdviseForTheNewAlert(Alert newAlert, Key<Alert> alertKey) throws UnsupportedEncodingException {
		String mailTo = URLEncoder.encode(Params.EMAIL_ADDRESS, "UTF-8");
		@SuppressWarnings("deprecation")
		String messageBody = URLEncoder.encode("Nouvelle alerte:"+
				"<br><br>" +
				"Titre: " + newAlert.getTitle() + "<br>"+
				"Code: " + alertKey.getId() + "<br>"+
				"Description: " + newAlert.getDescription() + "<br>"+
				"Adresse de la rue: " + newAlert.getAddress() +  "<br>"+
				"Email: " + newAlert.getCreationUser()+ 
				"<br><br>" +
				"***E-mail automatique, ne pas repondre***", "UTF-8");
		String subject = "["+Params.EMAIL_NAME+"] Nouvelle alerte";
		try {
			URL url = new URL(Params.PROJECT_URL_BASE+"/SendMailServlet");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write("mailTo=" + mailTo);
			writer.write("&subject=" + subject);
			writer.write("&messageBody=" + messageBody);
			writer.close();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.severe("Problem occurred sending email");
			}
		} catch (MalformedURLException e) {
			log.severe("Problem occurred sending email");
		} catch (IOException e) {
			log.severe("Problem occurred sending email");
		}
	}

	private void adviseForNewUser(HttpServletRequest req,HttpServletResponse resp, String rdmString, String email) throws UnsupportedEncodingException {
		String mailTo = URLEncoder.encode(Params.ADVICE_EMAIL_LIST, "UTF-8");
		@SuppressWarnings("deprecation")
		String messageBody = URLEncoder.encode("Bonjour, " + 
				"<br>Merci de vous être inscrit."+
				"<br><br>"
				+ "Votre mot de passe: <b>" + rdmString+ "</b><br><br>" +
				"Pour confirmer votre inscription, veuillez visiter " +
				"<a href='"+Params.PROJECT_URL_BASE+"AuthenticationServlet?email="+email+"&confirmationCode="+rdmString+"' target='_blank'>ce lien</a>" +
				"<br><br>" +
				"***E-mail automatique, ne pas repondre***", "UTF-8");
		String subject = "["+Params.EMAIL_NAME+"] Confirmation de l'inscription";
		try {
			URL url = new URL(Params.PROJECT_URL_BASE+"/SendMailServlet");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
			writer.write("mailTo=" + email);
			writer.write("&subject=" + subject);
			writer.write("&messageBody=" + messageBody);
			writer.close();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.severe("Problem occurred sending email");
			}
		} catch (MalformedURLException e) {
			log.severe("Problem occurred sending email");
		} catch (IOException e) {
			log.severe("Problem occurred sending email");
		}
	}
}
