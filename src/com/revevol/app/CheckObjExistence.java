package com.revevol.app;



import com.revevol.app.utils.Methods;
import com.revevol.app.utils.Params;
import com.revevol.app.utils.RESTresponse;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;






import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.Transform;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.gson.Gson;


public class CheckObjExistence extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3720092520877413070L;
	private static final Logger log = Logger.getLogger(CheckObjExistence.class.getName());
//	private final GcsService gcsService =
//		    GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try{
			//return response
			RESTresponse response=new RESTresponse(Params.HTTP_STATUS_501);
			resp.setContentType("application/json");
			resp.getWriter().print(new Gson().toJson(response));
		}
		catch(Exception ex){
			ex.printStackTrace();
			log.severe("An error occured: " + ex.getMessage() + "\n" + Methods.printStackStrace(ex));
		}
	}
	
//	public void rotateImage(String fileName) throws IOException{
//		
//		GcsFilename filename = new GcsFilename(Params.BUCKET_NAME, fileName);
//		
//		int fileSize = (int) gcsService.getMetadata(filename).getLength();
//	    ByteBuffer result = ByteBuffer.allocate(fileSize);
//	    try (GcsInputChannel readChannel = gcsService.openReadChannel(filename, 0)) {
//	      readChannel.read(result);
//	    }
//	    
//	    ByteArrayInputStream bis = new ByteArrayInputStream(result.array());
//	    BufferedInputStream bufis = new BufferedInputStream(bis);;
//	    
//	    Metadata metadata;
//	    //read file metadata
//	    try {
//			 metadata = ImageMetadataReader.readMetadata(bufis, true);
//			 
//			 //get orientation
//			 ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
//			 int orientation=directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
//			 int rotate=0;
//			switch (orientation) {
//				case 1: { //0°
//					rotate=0;
//					break;
//				}
//				case 8: { //90° CW
//					rotate=270;
//					break;
//				}
//				case 3: { //180° CW
//					rotate=180;
//					break;
//				}
//				case 6: { //270°CW
//					rotate=90;
//					break;
//				}
//			}
//			
//			ImagesService imagesService = ImagesServiceFactory.getImagesService();
//
//	        Image oldImage = ImagesServiceFactory.makeImage(result.array());
//	        Transform resize = ImagesServiceFactory.makeRotate(rotate);
//
//	        Image newImage = imagesService.applyTransform(resize, oldImage);
//
//	        byte[] newImageData = newImage.getImageData();
//	        
//	        GcsFilename outputfilename = new GcsFilename(Params.BUCKET_NAME, fileName);
//	        GcsOutputChannel outputChannel =
//	        	    gcsService.createOrReplace(outputfilename, GcsFileOptions.getDefaultInstance());
//	        	outputChannel.write(ByteBuffer.wrap(newImageData));
//	        	outputChannel.close();
//			 
//			 
//		}
//		catch (ImageProcessingException e) {
//			Methods.printStackStrace(e);
//		}
//	    catch(Exception ex){
//	    	Methods.printStackStrace(ex);
//	    }
//		
//	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//String fileName = req.getParameter("inputFile");
		//rotateImage(fileName);
		try {
			
			//load private key
//			InputStream privateKeyStream = null;
//						privateKeyStream = new FileInputStream(this.getServletContext().getRealPath(Params.REAL_KEY_PATH));
//			KeyStore ks = KeyStore.getInstance("PKCS12");
//					 ks.load(privateKeyStream, "notasecret".toCharArray());
//			PrivateKey myOwnKey=(PrivateKey)ks.getKey("privatekey", "notasecret".toCharArray());
//	     
//			final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
//		    final JsonFactory JSON_FACTORY = new JacksonFactory();
//		    
//		    List<String> listScopes = new ArrayList<String>();
//		    listScopes.add(Params.GCS_SCOPE);
//		     
//		    //create OAuth 2.0 credentials based on PrivateKey 
//		    GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
//	             .setJsonFactory(JSON_FACTORY)
//	             .setServiceAccountId(Params.GOOGLE_ACCESS_ID)
//	             .setServiceAccountScopes(listScopes)
//	             .setServiceAccountPrivateKey(myOwnKey)
//	             .build();
//		    
//		    //get access token
//		    credential.refreshToken();
//		    String accessToken=credential.getAccessToken();
//	     
//		    //get current ACL
//		    String fileName=URLEncoder.encode(req.getParameter(Params.HTTP_PARAM_INPUT_FILE),"UTF-8");
//		    
//		  //set new rule
//			URL myURL=new URL(Params.PROTOCOL_S+"://"+Params.BUCKET_NAME+"."+Params.GOOGLE_STORAGE_API+"/"+fileName+"?acl");
//		    HTTPRequest put = new HTTPRequest(myURL,HTTPMethod.GET);
//		    	//set headers
//		    	put.addHeader(new HTTPHeader("Host", Params.BUCKET_NAME+"."+Params.GOOGLE_STORAGE_API));			
//		    	put.addHeader(new HTTPHeader("Authorization","OAuth " + accessToken));
//  			    put.addHeader(new HTTPHeader("Content-Length", "0"));
//  			    put.addHeader(new HTTPHeader("Date", Methods.getCurrentDate()));
//		    	put.addHeader(new HTTPHeader("x-goog-api-version", Params.GCS_REST_API_VERSION));
//		    	
//		    URLFetchService restService = URLFetchServiceFactory.getURLFetchService();
//		    HTTPResponse restResponse = restService.fetch(put);
		    
		    //return response
		    resp.setContentType("text/html");
		    resp.getWriter().print("");//restResponse.getResponseCode());
				
		}
		catch(Exception ex){
			ex.printStackTrace();
			log.warning("An error occured: " + ex.getMessage() + "\n" + Methods.printStackStrace(ex));
		}
	}
}
