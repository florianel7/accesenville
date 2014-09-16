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

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.revevol.app.model.Alert;
import com.revevol.app.model.EntityParent;
import com.revevol.app.model.Message;
import com.revevol.app.model.Metadata;
import com.revevol.app.model.Peba;
import com.revevol.app.model.User;
import com.revevol.app.utils.Params;

public class PebaServlet extends HttpServlet {

	private static final Logger	log	= Logger.getLogger(PebaServlet.class.getName());
	private Gson gson = new Gson();


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		ObjectifyService.register(EntityParent.class);
		ObjectifyService.register(Peba.class);

		resp.setContentType("text/html; charset=UTF-8");
		if("LIST_PEBA".equals(req.getParameter("method"))){
			getListPeba(req,resp);
		}
		else if("SAVE_PEBA".equals(req.getParameter("method"))){
			createPeba(req,resp);
		}
	}

	private void getListPeba(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		List<Peba> pebaList = ofy().load().type(Peba.class).filter("deleted", false).list();
		resp.getWriter().print(gson.toJson(pebaList));
	}

	private void createPeba(HttpServletRequest req,HttpServletResponse resp) throws IOException {
		Peba newPeba = gson.fromJson(req.getParameter("peba"), Peba.class);
		ofy().save().entity(newPeba).now();
		resp.getWriter().print("OK");
	}
}
