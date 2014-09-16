package com.revevol.app;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revevol.app.model.Alert;
import com.revevol.app.model.Authorization;
import com.revevol.app.model.EntityParent;
import com.revevol.app.model.Metadata;
import com.revevol.app.model.User;
import com.revevol.app.utils.BCrypt;
import com.revevol.app.utils.Params;
import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class AuthenticationServlet extends HttpServlet {

	private static final Logger	log	= Logger.getLogger(AuthenticationServlet.class.getName());
	private Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		if(req.getParameter("confirmationCode") != null){
			confirmRegistration(req, resp);
		}
		else{
			doPost(req, resp);
		}
	}

	/**
	 * Confirm a registration of a new user controlling the confirmationCode sent by email
	 * @param req
	 * @throws IOException 
	 */
	private void confirmRegistration(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String confirmationCode = req.getParameter("confirmationCode");
		String userEmail = req.getParameter("email");
		userEmail = userEmail.toLowerCase();
		User userToConfirm = ofy().load().type(User.class).id(userEmail).now();
		if(userToConfirm != null){
			if(userToConfirm.getConfirmationCode().equals(confirmationCode)){
				userToConfirm.setActive(true);
				ofy().save().entity(userToConfirm).now();
				
				//now we update all the alert of the user to true
				List<Alert> alertListByUser = ofy().load().type(Alert.class).filter("creationUser", userToConfirm.getEmail()).list();
				for (Alert alert : alertListByUser) {
					alert.setActive(true);
					ofy().save().entity(alert);
				}
				
				resp.sendRedirect(Params.PROJECT_URL_BASE+"#/tab/register-ok");
			}
			else{
				resp.sendRedirect(Params.PROJECT_URL_BASE+"#/tab/register-ko");
			}
		}
		else{
			resp.sendRedirect(Params.PROJECT_URL_BASE+"#/tab/register-ko");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		ObjectifyService.register(EntityParent.class);
		ObjectifyService.register(User.class);
		ObjectifyService.register(Authorization.class);
		
		resp.setContentType("text/html; charset=UTF-8");
		if("NEW_REGISTRATION".equals(req.getParameter("method"))){
			newRegistration(req,resp);
		}
		else if("LOGIN".equals(req.getParameter("method"))){
			login(req,resp);
		}
	}

	private void login(HttpServletRequest req, HttpServletResponse resp) {
		try {
			User userJson = gson.fromJson(req.getParameter("user"), User.class);
			if(userJson.getEmail() != null){
				userJson.setEmail(userJson.getEmail().toLowerCase());
				User user = ofy().load().type(User.class).id(userJson.getEmail()).now();
				if(user != null
					&& userJson.getPassword() != null){
					if (userJson.getPassword().equals(user.getPassword())){
						user.setPassword("");
						if(user.isActive()){
							//set the token and time expiration
							String newToken = createAuth(req, resp , user);
							user.setToken(newToken);
						}
						resp.getWriter().print(gson.toJson(user));
					}
					else{
						resp.getWriter().print("WRONG");
					}
				}
				else{
					resp.getWriter().print("WRONG");
				}
			}
			else{
				resp.getWriter().print("WRONG");
			}
		} catch (Exception e) {
			try {
				//Logger.getLogger(AuthenticationServlet.class.toString()).warning("Exception:" +e.getCause());
				resp.getWriter().print("ERROR");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private String createJBCryptToken(String emailUser) {
		String dateNowString = new Date().toString();
		String compositeString = emailUser+dateNowString;

		String newToken = BCrypt.hashpw(compositeString, BCrypt.gensalt());
		newToken = URLEncoder.encode(newToken);
		return newToken;
	}

	private String createAuth(ServletRequest req, ServletResponse res, User user) throws IOException{

		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, 3); // adds 3 hours
		// returns new date object, one hour in the future
		String token = createJBCryptToken(user.getEmail());
		
		Authorization newAuth = new Authorization();
		newAuth.setCreationTime(new Date());
		newAuth.setEmail(user.getEmail());
		newAuth.setExpiredTime(cal.getTime());
		newAuth.setToken(token);

		ofy().save().entity(newAuth);
		
		return newAuth.getToken();
	}

	private void newRegistration(HttpServletRequest req,HttpServletResponse resp) {
		try {
			Random rdm = new Random();
			String rdmString  = new Integer(rdm.nextInt(999999999)).toString();
			User userJson = gson.fromJson(req.getParameter("user"), User.class);
			userJson.setEmail(userJson.getEmail().toLowerCase());
			userJson.setConfirmationCode(rdmString);
			//Date birthdate = new SimpleDateFormat("dd/MM/yyyy").parse((String) userJson.get("birthdate"));
			if(ofy().load().type(User.class).id(userJson.getEmail()).now() == null){
				ofy().save().entity(userJson).now();
				resp.getWriter().print("OK");
				adviseForNewUser(req , resp , rdmString);
			}
			else{
				resp.getWriter().print("ALREADY_EXISTS");
			}
		} catch (Exception e) {
			try {
				resp.getWriter().print("ERROR");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void adviseForNewUser(HttpServletRequest req,HttpServletResponse resp, String rdmString) throws UnsupportedEncodingException {
		String user = req.getParameter("user");
		User userJson = gson.fromJson(req.getParameter("user"), User.class);
		String userEmail = (String) userJson.getEmail();
		String userName = (String) userJson.getName();
		String userSurname = (String) userJson.getSurname(); 

		String mailTo = URLEncoder.encode(Params.ADVICE_EMAIL_LIST, "UTF-8");
		@SuppressWarnings("deprecation")
		String messageBody = URLEncoder.encode("Bonjour " + userName+","+ 
				"<br>Merci de vous être inscrit."+
				"<br><br>" +
				"Pour confirmer votre inscription, veuillez visiter " +
				"<a href='"+Params.PROJECT_URL_BASE+"AuthenticationServlet?email="+userEmail+"&confirmationCode="+rdmString+"' target='_blank'>ce lien</a>" +
				"<br><br>" +
				"***E-mail automatique, ne pas repondre***", "UTF-8");
		String subject = "["+Params.EMAIL_NAME+"] Confirmation de l'inscription";
		try {
			URL url = new URL(Params.PROJECT_URL_BASE+"/SendMailServlet");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write("mailTo=" + userEmail);
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
