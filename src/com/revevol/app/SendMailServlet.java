package com.revevol.app;

import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.revevol.app.utils.Params;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class SendMailServlet extends HttpServlet {

	private static final long serialVersionUID = -1779720118940455959L;
	private static final Logger	log	= Logger.getLogger(SendMailServlet.class.getName());

	/**
	 * GET Method
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		resp.setContentType("text/html; charset=UTF-8");
		try{
			//call post - only get allowed
			doPost(req,resp); 
		}
		catch(Exception e){
			log.severe("*** An exception has occured *** : " + e.getLocalizedMessage() + "\n" + e.getCause());
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res){
		res.setContentType("text/html; charset=UTF-8");
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try{		
			String [] emailAddresses=req.getParameter("mailTo").trim().split(",");
			for(int i=0;i<emailAddresses.length;i++){
				if(!emailAddresses[i].equals("")){
					int indexTransaction = 0;
					while (indexTransaction < 4 ) {
						try {

							Multipart mp = new MimeMultipart(); 
							MimeBodyPart htmlPart = new MimeBodyPart(); 
							htmlPart.setContent(req.getParameter("messageBody").toString(), "text/html"); 
							mp.addBodyPart(htmlPart); 


							//prepare mail
							Message msg = new MimeMessage(session);
							//from
							msg.setFrom(new InternetAddress(Params.EMAIL_NAME+" <"+Params.EMAIL_ADDRESS+">"));
							//to
							msg.addRecipient(Message.RecipientType.TO,new InternetAddress(emailAddresses[i]));
							//subject
							msg.setSubject(req.getParameter("subject"));
							//body
							msg.setContent(mp);
							
							Transport.send(msg);

							//prepare response
							JSONObject jsonResult=new JSONObject();
							jsonResult.put("statusCode", "0");

							//return data
							res.setContentType(Params.APPLICATION_JSON);
							res.getOutputStream().write(jsonResult.toString().getBytes(),0,jsonResult.toString().length());
							indexTransaction=4;

							log.info("EMAIL SENT: " + emailAddresses[i]+ " " + req.getParameter("subject") + "\n\n" + req.getParameter("messageBody").toString());
						}
						catch (Exception e) {

							StringBuilder errMsg=new StringBuilder();
							errMsg.append("*** Transaction #" + indexTransaction + " : an exception has occured during execution  *** : ");
							errMsg.append(e.getLocalizedMessage());
							errMsg.append("\n");
							errMsg.append("===========\n");
							errMsg.append("PARAMETERS:\n");
							errMsg.append("===========\n");
							errMsg.append("mailTo: " + req.getParameter("mailTo") + "\n");
							errMsg.append("mailToName: " + req.getParameter("mailToName") + "\n");
							errMsg.append("subject: " + req.getParameter("subject") + "\n");
							errMsg.append("messageBody: " + req.getParameter("messageBody") + "\n");
							errMsg.append("invoiceGoogleID: " + req.getParameter("invoiceGoogleID") + "\n");
							errMsg.append("invoiceName: " + req.getParameter("invoiceName") + "\n");
							errMsg.append("\n");
							errMsg.append(e.getCause());

							log.severe(errMsg.toString());

							indexTransaction++;
						}
					}

				}

			}
		}
		catch(Exception ex){
			log.severe("*** An exception has occured *** ERROR SERVLET: " + ex.getLocalizedMessage() + "\n" + ex.getMessage());
		}
	}
}
