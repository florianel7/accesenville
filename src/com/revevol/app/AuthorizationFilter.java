package com.revevol.app;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.googlecode.objectify.ObjectifyService;

import com.revevol.app.model.Alert;
import com.revevol.app.model.Authorization;
import com.revevol.app.model.EntityParent;
import com.revevol.app.model.User;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class AuthorizationFilter implements Filter {

	private static final Logger	log	= Logger.getLogger(AuthorizationFilter.class.getName());

	@SuppressWarnings("unused")
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		
		chain.doFilter(req, res);
		
//		HttpServletRequest httpRequest = (HttpServletRequest) req;
//		String path = httpRequest.getRequestURI();
//
//		res.setContentType("text/html; charset=UTF-8");
//		if("/UploadFileServlet".equals(path)){
//			String method = req.getParameter("method");
//			if(method.equals("GET_LIST_FILE_BY_ALERT")){
//				//the list of all the alert is public it is public
//				chain.doFilter(req, res);
//			}
//		}
//		else if("/AlertServlet".equals(path)){
//			String method = req.getParameter("method");
//			if(method.equals("LIST_ALERT")
//				|| method.equals("NEW_USER_ALERT")){
//				//the list of all the alert is public it is public
//				chain.doFilter(req, res);
//			}
//		}
//		else if("/AuthenticationServlet".equals(path)
//			|| path.contains("/_ah/")){
//			//if login go directly to login
//			chain.doFilter(req, res);
//		}
//		else if("/SendMailServlet".equals(path)
//				|| "/GcsServlet".equals(path)
//				|| "/CheckObjExistence".equals(path)
//				|| "/createadmin".equals(path)){
//			//if email servlet or file uploading must have normal request
//			chain.doFilter(req, res);
//		}
//		else{
//			controllingAuth(req,res,chain);
//		}

	}

	/**
	 * control if the token and date expired is ok
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void controllingAuth(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		String response = "TIME_IS_EXPIRED";
		try {
			Authorization myAuth = controllingExpirationTime(req, res);
			if(myAuth != null){
				// if exist an authorization with this email and this token 
				// we must control if the date is expired or not
				Date expirationTime = myAuth.getExpiredTime();
				if(expirationTime != null){
					if(expirationTime.after(new Date())){
						// if expiration time is ok we updated the DB
						updateExpirationTime(req, res , myAuth);
						chain.doFilter(req, res);
					}
					else{
						res.getWriter().print(response);
					}
				}
				else{
					res.getWriter().print(response);
				}
			}
			else{
				res.getWriter().print(response);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Authorization controllingExpirationTime(ServletRequest req,ServletResponse res) {
		ObjectifyService.register(EntityParent.class);
		ObjectifyService.register(Alert.class);
		ObjectifyService.register(Authorization.class);
		String emailUser = req.getParameter("emailUser");
		String token = req.getParameter("token");
		
		token = URLEncoder.encode(token);

		Authorization auth = ofy().load().type(Authorization.class).id(token).now();
		if(auth != null){
			return auth;
		}
		return null;
	}

	private void updateExpirationTime(ServletRequest req, ServletResponse res, Authorization myAuth) {
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, 3); // adds 3 hours
		// returns new date object, one hour in the future
		Date expirationTimeUpdated = cal.getTime();

		myAuth.setExpiredTime(expirationTimeUpdated);
		ofy().save().entity(myAuth);
	}

	public void init(FilterConfig config) throws ServletException {
		//start first time when application start
	}
	public void destroy() {
		//add code to release any resource
	}


}

