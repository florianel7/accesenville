package com.revevol.app;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//The Enqueue servlet should be mapped to the "/enqueue" URL.
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import com.google.appengine.api.taskqueue.QueueFactory;

public class Enqueue extends HttpServlet {
	
	private static final Logger	log	= Logger.getLogger(Enqueue.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	log.severe("TASK QUEUE STARTED");
        String key = request.getParameter("password");

        // Add the task to the default queue.
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl("/ImportFromExcel").param("password", key));

        response.sendRedirect("/");
    }
}
