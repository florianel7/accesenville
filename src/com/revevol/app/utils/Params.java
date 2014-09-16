package com.revevol.app.utils;

public class Params {
	
	
	/**
	 * EMAIL CONFIGURATIONS
	 */
	public static final String EMAIL_NAME = "Acces En Ville";
	public static final String EMAIL_ADDRESS = "fattura_cc@revevolitalia.info";
	
	
	public static final String ADVICE_EMAIL_LIST = "florianel@google.com"; // email that receive new user
	public static final String PASSWORD = "accessenville!!!";
	
	//public static final String PROJECT_URL_BASE = "http://localhost:8888/";
	public static final String PROJECT_URL_BASE = "https://accesenville.appspot.com/";
	

	
	/**
	 * APP PARAMS
	 */
	public static final String APPLICATION_JSON	= "application/json";
	public static final int STANDARD_TRANSACTIONS = 10;
	
	public static final String GAPPS_USER_NAME = "fattura_cc@revevolitalia.info";
	
	// Google Cloud Storage Parameters
	public static final String	PROTOCOL_S				= "https";
	public static final String	PROTOCOL				= "http";
	public static final String	GOOGLE_STORAGE_API		= "storage.googleapis.com";
	public static final String	BUCKET_NAME				= "access-en-ville";
	public static final String	GOOGLE_ACCESS_ID		= "992016922423-48a48a44301oo2fbvkehnaf777511qpg@developer.gserviceaccount.com";
	
	
	public static final String	GCS_SCOPE				= "https://www.googleapis.com/auth/devstorage.full_control";
	public static final String	ACL_PUBLIC_READ			= "<Entry><Scope type='AllUsers'/><Permission>READ</Permission></Entry>";
	public static final String	ACL_END_ENTRY			= "</Entries>";
	public static final String	GCS_REST_API_VERSION	= "2";
	public static final String	HTTP_PARAM_INPUT_FILE	= "inputFile";
	public static final String	HTTP_STATUS_200			= "200 OK";
	public static final String	HTTP_STATUS_204			= "204 No content";
	public static final String	HTTP_STATUS_501			= "501 Method not recognized";
	public static final String	HTTP_STATUS_401			= "401 Unauthorized";

	public static final String REAL_KEY_PATH = "WEB-INF/key/project_key.p12";
	
	
}
