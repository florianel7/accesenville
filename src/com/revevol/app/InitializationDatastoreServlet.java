package com.revevol.app;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revevol.app.model.EntityParent;
import com.revevol.app.model.User;
import com.revevol.app.utils.BCrypt;
import com.revevol.app.utils.Params;
import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class InitializationDatastoreServlet extends HttpServlet {
	
	private static final Logger	log	= Logger.getLogger(InitializationDatastoreServlet.class.getName());
	private Gson gson = new Gson();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		ObjectifyService.register(EntityParent.class);
		ObjectifyService.register(User.class);
		
		resp.setContentType("text/html; charset=UTF-8");
		createAdministratorInDatastore(req,resp);
	}

	private void createAdministratorInDatastore(HttpServletRequest req,HttpServletResponse resp) {
		try {
			Date birthdate = new SimpleDateFormat("dd/MM/yyyy").parse((String) "01/01/2014");
			User adminUser = new User("Administrator",
					Params.ADVICE_EMAIL_LIST,
					birthdate,
					"Admin",
					Params.ADVICE_EMAIL_LIST,
					Params.PASSWORD,
					"Admin",
					true,
					"",
					"");
			if(ofy().load().type(User.class).id(Params.ADVICE_EMAIL_LIST).now() == null){
				ofy().save().entity(adminUser).now();
				resp.getWriter().print("Administrator created correctly!");
			}
			else{
				resp.getWriter().print("The Administrator already exists");
			}
		} catch (Exception e) {
			try {
				resp.getWriter().print("ERROR");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
