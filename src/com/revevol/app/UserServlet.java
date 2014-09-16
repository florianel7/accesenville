package com.revevol.app;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revevol.app.model.EntityParent;
import com.revevol.app.model.User;
import com.revevol.app.utils.Params;

import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class UserServlet extends HttpServlet {
	private Gson gson = new Gson();
	private static final Logger	log	= Logger.getLogger(UserServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		ObjectifyService.register(EntityParent.class);
		ObjectifyService.register(User.class);
		
		resp.setContentType("text/html; charset=UTF-8");
		if("UPDATE_USER".equals(req.getParameter("method"))){
			updateUser(req,resp);
		}
		else if("GET_ALL_USER".equals(req.getParameter("method"))){
			getAllUser(req,resp);
		}
	}

	private void getAllUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<User> userList = ofy().load().type(User.class).list();
		for (User user : userList) {
			user.setPassword("");
		}
		resp.getWriter().print(gson.toJson(userList));
	}

	private void updateUser(HttpServletRequest req, HttpServletResponse resp) {
		try {
			User userJson = gson.fromJson(req.getParameter("userInEditing"), User.class);
			User datastoreUser = ofy().load().type(User.class).id(userJson.getEmail()).now();
			datastoreUser.setActive(userJson.isActive());
			ofy().save().entity(datastoreUser).now();
			resp.getWriter().print("OK");
		} catch (Exception e) {
			try {
				resp.getWriter().print("ERROR");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void adviseForActivation(HttpServletRequest req,HttpServletResponse resp) throws UnsupportedEncodingException {
		String userInEditing = req.getParameter("userInEditing");
		User userJson = gson.fromJson(req.getParameter("userInEditing"), User.class);
		String userEmail = (String) userJson.getEmail();
		String userName = (String) userJson.getName();
		String userPassword = (String) userJson.getPassword();
		boolean userActive = (Boolean) userJson.isActive();
		
		if(userActive){
			String mailTo = URLEncoder.encode(userEmail, "UTF-8");
			@SuppressWarnings("deprecation")
			String messageBody = URLEncoder.encode("Dear " + userName +","+
												   "<br>we have just enabled your activation request." +
												   "<br>Here is your login credentials:<br>" +
												   "<br>Email: " + userEmail +
												   "<br>Password: " + userPassword +
												   "<br><br>" +
												   "Thank you for registering." +
												   "<br><br><br><br>" +
												   "***This is an automatic email - Do not answer***", "UTF-8");
			String subject = "["+Params.EMAIL_NAME+"] Utenza attivata correttamente";
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
		
	}

}
