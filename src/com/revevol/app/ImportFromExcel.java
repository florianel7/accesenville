package com.revevol.app;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.cloud.sql.jdbc.internal.Util;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.ObjectifyService;
import com.revevol.app.model.Alert;
import com.revevol.app.model.Message;
import com.revevol.app.model.PojoAlert;
import com.revevol.app.utils.Methods;

import jxl.Cell;
import jxl.CellType;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

@SuppressWarnings("serial")
public class ImportFromExcel extends HttpServlet {
	private static final Logger	log	= Logger.getLogger(ImportFromExcel.class.getName());
	
	private int count = 0;
	final Geocoder geocoder = new Geocoder();
	List<PojoAlert> outputAlert = new ArrayList<PojoAlert>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		String password = req.getParameter("password");
		if("test".equals(password)){
			ObjectifyService.register(Alert.class);
			deleteOldImportedAlert();
			createCollectionFormExcel(resp);
			log.severe("IMPORT FINISHED CORRECTLY!!!");
		}
		else if("latlgt".equals(password)){
			updateLatLgt();
			resp.getWriter().print("LAT LANG FINISH CORRECTLY");
		}
		else{
			log.severe("PASSWORD NOT CORRECT...");
		}
	}

	/**
	 * Delete old imported alert
	 */
	private void deleteOldImportedAlert() {
		log.severe("INIT DELETION ALL IMPORTED EXCEL DATA");
		ofy().delete().entities(ofy().load().type(Alert.class).filter("importedFromExcel", true).keys());
		log.severe("FINISH DELETION ALL IMPORTED EXCEL DATA");
	}

	private String createCollectionFormExcel(HttpServletResponse resp) throws IOException {
		List<String[]> listFromExcel = createListFromExcel();
		count = 0;
		outputAlert = new ArrayList<PojoAlert>();
		saveIntoTheDatastore(listFromExcel,resp);
		resp.getWriter().print(new Gson().toJson(outputAlert));
		return null;
	}

	

	private void saveIntoTheDatastore(List<String[]> listFromExcel, HttpServletResponse resp) throws IOException {
		
		if(listFromExcel != null){
			
			log.severe("INIT LOOP");
			while(listFromExcel.size() > count){
				String[] row = listFromExcel.get(count);
//				try {
					
					String street = row[3] + " , Italy";
					log.severe("STREET RESULT: " + street);
					/**
					 * Get coordinates from description
					 * 
					 * geocoderResponse != null
						&& "OK".equals(geocoderResponse.getStatus().name())
							&& geocoderResponse.getResults() != null
								&& geocoderResponse.getResults().size() > 0 
								
					 */
					GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(street).setLanguage("en").getGeocoderRequest();
					GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
					if(true){
						saveAlert(row, new Double(0),
								new Double(0));
						
						count++;
					}
					else{
						log.severe(geocoderResponse.getStatus().name());
						log.severe("NOT EXECUTED n."+ row[0]);
						saveIntoTheDatastore(listFromExcel,resp);
					}
					
					
				    //thread to sleep for the specified number of milliseconds
//				    Thread.sleep(2000);
//				} catch ( java.lang.InterruptedException ie) {
//					log.severe(Methods.printStackStrace(ie));
//					log.severe(ie.getLocalizedMessage());
//				    System.out.println(ie);
//				}
				
			}
			log.severe("FINISH LOOP");
		}
	}
	
	private void saveAlert(String[] row ,Double lat , Double lgt){
		Alert alert = new Alert();
		alert.setActive(true);
		alert.setImportedFromExcel(true);
		alert.setEnabled(true);
		alert.setTitle(row[3] + " (cod. "+row[0]+")");
		alert.setDescription(row[6]);
		alert.setAddress(row[3]);
		alert.setCreationUser(row[4]);
		alert.setCreationDate(new Date().getTime());
		alert.setUserInformationsImported(row[1]+ " " + row[2] +  " - Telefono: " + row[5]);
		alert.setLatitude(lat);
		alert.setLongitude(lgt);
		log.severe("SAVED ALERT n."+ alert.getTitle() + " - coordinates: " + lat + " " + lgt);
		
		PojoAlert test = new PojoAlert();
		test.setLat(alert.getLatitude());
		test.setLgt(alert.getLongitude());
		test.setEmail(alert.getCreationUser());
		outputAlert.add(test);
	    ofy().save().entity(alert);
	}

	private String getStreet(String string) {
		if(string != null
			&& !string.trim().equals(".")){
			return string + " , ";
		}
		return "";
	}

	/**
	 * Leggo il file excel e per ogni riga creo il mio oggetto
	 * @return
	 */
	private List<String[]> createListFromExcel() {
		List<String[]> list = new ArrayList<String[]>();
		try {
			Workbook workbook = Workbook.getWorkbook(new File("import20140430.xls"));
			Sheet sheet = workbook.getSheet(0);
			for (int i = 1; i < 116; i++) {    // riga
				String[] myObj = new String[7];
				for (int j = 0; j < 7; j++) { // colonna 
					Cell cell = sheet.getCell(j,i);
					myObj[j] = cell.getContents();
				}
				list.add(myObj);
			}
			return list;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	class Id<T> {
		   private final Class<T> clazz;
		   private final long value;
		   public Id(Class<T> clazz, long value) {
		     this.clazz = clazz;
		     this.value = value;
		   }
		 }
	
	class IdInstanceCreator implements InstanceCreator<Id> {
		   public Id createInstance(Type type) {
		     return new Id(Object.class, 0L);
		   }
		 }
	
	private void updateLatLgt() {
		//Gson gson = new GsonBuilder().registerTypeAdapter(Test.class, new IdInstanceCreator()).create();
		Type listOfTestObject = new TypeToken<List<PojoAlert>>(){}.getType();
		ArrayList<PojoAlert> list = new Gson().fromJson(text, listOfTestObject);
		for (PojoAlert test : list) {
			List<Alert> alertList = ofy().load().type(Alert.class).filter("creationUser", test.getEmail()).list();
			if(alertList != null
					&& alertList.size() > 0 ){
				for (Alert alert : alertList) {
					alert.setLatitude(test.getLat());
					alert.setLongitude(test.getLgt());
					ofy().save().entity(alert);
				}
			}
		}
	}
	
	String text ="[{'email':'polloni.patrizia@gmail.com','lat':45.4654542,'lgt':9.186516},{'email':'invernizzi_diego@libero.it','lat':45.4654542,'lgt':9.186516},{'email':'federico.gia@libero.it','lat':41.60866439999999,'lgt':13.0821906},{'email':'albini.d@gmail.com ','lat':45.4655261,'lgt':8.8850211},{'email':'giocampione@yahoo.it','lat':37.31604799999999,'lgt':13.6623483},{'email':'giancarlo.maffioli@alice.it','lat':45.496531,'lgt':9.3750131},{'email':'beppe29755@yahoo.it','lat':45.0272801,'lgt':7.5107776},{'email':'f.corriere@virgilio.it','lat':44.4056499,'lgt':8.946256},{'email':'rotunnoida@hotmai.it','lat':45.070312,'lgt':7.686856499999999},{'email':'lara.belcastro@gmail.com','lat':40.914388,'lgt':14.7906121},{'email':'gnicosia@inwind.it','lat':44.494887,'lgt':11.3426163},{'email':'stella_marina78@hotmail.com','lat':40.9416293,'lgt':14.2759621},{'email':'azzurrocielo1@hotmail.it','lat':45.736778,'lgt':9.125384},{'email':'vlussu@tiscali.it','lat':39.462863,'lgt':8.7411909},{'email':'stefania.caratti@gmail.com','lat':44.90075119999999,'lgt':8.2064257},{'email':'gfrazzetto@libero.it','lat':37.1613188,'lgt':14.7488678},{'email':'mariatscn896@gmai.com','lat':38.1156879,'lgt':13.3612671},{'email':'grossipezza@tiscali.it','lat':41.8723889,'lgt':12.4801802},{'email':'robertoleccese55@libero.it','lat':40.3515155,'lgt':18.1750161},{'email':'assuntadangelo@tiscli.it','lat':40.53049770000001,'lgt':17.5827818},{'email':'beumiki@libero.it','lat':42.540938,'lgt':14.123369},{'email':'flavia.depaoli@libero.it','lat':45.1931241,'lgt':11.210085},{'email':'raffaele-caldarelli@libero.it','lat':40.8767024,'lgt':14.3411568},{'email':'ianni.raff@gmail.com','lat':41.8723889,'lgt':12.4801802},{'email':'dana.relli@libero.it','lat':38.1113006,'lgt':15.6472914},{'email':'graziarita61@gmail.com','lat':40.32024210000001,'lgt':9.3264377},{'email':'lore_faro@hotmail.it','lat':45.4654542,'lgt':9.186516},{'email':'ottavio.sollevante@alice.it','lat':44.6989932,'lgt':10.6296859},{'email':'gianluigipro@hotmail.it','lat':40.3267136,'lgt':17.5729577},{'email':'gabrydelking@hotmail.it','lat':45.6084736,'lgt':8.1740782},{'email':'raffaella_dimarco@msn.com','lat':45.4408474,'lgt':12.3155151},{'email':'baeta66@gmail.com','lat':45.7657286,'lgt':11.7272747},{'email':'baeta66@gmail.com','lat':45.7657286,'lgt':11.7272747},{'email':'marfanangioletti@tiscali.it','lat':39.3112473,'lgt':8.9662188},{'email':'nonsolomusicachivasso@gmail.com','lat':45.1503148,'lgt':8.0150813},{'email':'mariorepetto@tiscali.it','lat':44.4056499,'lgt':8.946256},{'email':'citro.lucia@libero.it','lat':45.4654542,'lgt':9.186516},{'email':'cinziadifazio@hotmail.it','lat':41.8361406,'lgt':12.9733452},{'email':'violastralontanoò@libero.it','lat':44.494887,'lgt':11.3426163},{'email':'corrado.depaolis52@yahoo.it','lat':40.1470984,'lgt':18.19663},{'email':'luna78rossa@hotmail.it','lat':45.06606319999999,'lgt':7.578910299999999},{'email':'massimomazzarini@aluce.it','lat':42.0924239,'lgt':11.7954132},{'email':'toto.merola@alice.it','lat':40.4248153,'lgt':15.0769699},{'email':'miliucci@miliucci.it','lat':40.620022,'lgt':14.9478622},{'email':'laratriv@virgilio.it','lat':41.8723889,'lgt':12.4801802},{'email':'marzolalaura@libero.it','lat':43.3048492,'lgt':13.7218351},{'email':'adimuoio1@alice.it','lat':40.0732469,'lgt':15.6290106},{'email':'info@ristorantelacantinaza.com','lat':43.7367408,'lgt':12.9473755},{'email':'movisicilia@gmail.com','lat':36.9520998,'lgt':14.5372653},{'email':'pizzornomario@virgilio.it','lat':44.6160512,'lgt':8.9445223},{'email':'corrado.depaolis52@yahoo.it','lat':40.1470984,'lgt':18.19663},{'email':'corrado.depaolis52@yahoo.it','lat':40.1470984,'lgt':18.19663},{'email':'montavo@libero.it','lat':40.7379299,'lgt':13.9486184},{'email':'lssndr.ppn@gmail.com','lat':41.8723889,'lgt':12.4801802},{'email':'mazzellaernesto@libero.it','lat':40.7379299,'lgt':13.9486184},{'email':'gexigo@tin.it','lat':40.4086111,'lgt':17.2033333},{'email':'gexigo@tin.it','lat':40.4086111,'lgt':17.2033333},{'email':'salvatorebontempo@hotmail.it','lat':45.0021286,'lgt':7.655573},{'email':'albertocacciatore@libero.it','lat':40.0509542,'lgt':18.1252971},{'email':'stagnopatrizia63@tiscali.it','lat':40.92357639999999,'lgt':9.4964429},{'email':'cannella70@yahoo.it','lat':40.8517746,'lgt':14.2681244},{'email':'frida1246@hotmail.it','lat':41.5698647,'lgt':13.336405},{'email':'lambertig1951@gmail.com','lat':40.8517746,'lgt':14.2681244},{'email':'laurenzaagnese@libero.it','lat':40.9416293,'lgt':14.2759621},{'email':'cesare.balestra1@tin.it','lat':42.9853425,'lgt':13.8683671},{'email':'irene_de_santis@libero.it','lat':40.3515155,'lgt':18.1750161},{'email':'demasere@libero.it','lat':45.06606319999999,'lgt':7.578910299999999},{'email':'franco.mancino.26@alice.it','lat':41.0820867,'lgt':14.2541872},{'email':'sebastiano.sandri@alice.it','lat':45.7045749,'lgt':11.2241883},{'email':'info@arservizi.org','lat':44.7838779,'lgt':10.8796629},{'email':'poletto.ida@gimail.com','lat':45.4594916,'lgt':11.3851725},{'email':'sergio.ferrera@alice.it','lat':45.5658897,'lgt':8.9242729},{'email':'fenice2007@live.it','lat':43.7701635,'lgt':11.1128957},{'email':'lorusso-massimo@alice.it','lat':45.4654542,'lgt':9.186516},{'email':'contiemilio@tiscali.it','lat':41.60866439999999,'lgt':13.0821906},{'email':'gennaropalmieri@alice.it','lat':41.2774857,'lgt':16.4178334},{'email':'cesare.pani@fastwebnet.it','lat':45.4654542,'lgt':9.186516},{'email':'mancuso_franco@libero.it','lat':38.1156879,'lgt':13.3612671},{'email':'argialuisa@libero.it','lat':41.1061258,'lgt':14.2130486},{'email':'silviasapori.60@gmail.com','lat':44.1645688,'lgt':10.890991},{'email':'silviasapori.60@gmail.com','lat':44.1645688,'lgt':10.890991},{'email':'antonio.loiacono89@virgilio.it','lat':38.1938137,'lgt':15.5540152},{'email':'casapaolaepietro@alice.it','lat':40.8517746,'lgt':14.2681244},{'email':'anna.depasquale@telecomitalia.it','lat':41.7700866,'lgt':12.6585371},{'email':'morettisandra@hotmail.com','lat':41.8723889,'lgt':12.4801802},{'email':'lucian.ricciard@libero.it','lat':41.0856165,'lgt':16.0773816},{'email':'sanna67@alice.it','lat':41.8723889,'lgt':12.4801802},{'email':'bepomarangon@yahoo.it','lat':45.9255326,'lgt':13.4028485},{'email':'pgriesi@hotmail.com','lat':40.9607672,'lgt':15.815423},{'email':'otello.salvemme@gmail.com','lat':41.8723889,'lgt':12.4801802},{'email':'margheritagio@iol.it','lat':41.959817,'lgt':12.8022261},{'email':'ggsc2014@gmail.com','lat':38.1113006,'lgt':15.6472914},{'email':'lukiano@alice.it','lat':40.7894429,'lgt':14.367461},{'email':'admir2014@libero.it','lat':40.6327278,'lgt':17.9417616},{'email':'oiflaca@gmail.com','lat':37.6162261,'lgt':15.0786511},{'email':'roberto.diiorio@alice.it','lat':41.0723484,'lgt':14.3311337},{'email':'cescond@cescond.org','lat':40.1490282,'lgt':16.2874477},{'email':'s.romagnolo@romagnolo.it','lat':45.070312,'lgt':7.686856499999999},{'email':'rkl@coldmail.ar','lat':45.4654542,'lgt':9.186516},{'email':'argatone@gmail.com','lat':41.8723889,'lgt':12.4801802},{'email':'nicotraracin@live.it','lat':37.4831552,'lgt':13.9880975},{'email':'fiorello_dicampli@libero.it','lat':42.22655,'lgt':14.3890227},{'email':'francesco.magro.1cu8@alice.it','lat':38.90979189999999,'lgt':16.5876516},{'email':'ach.barna@libero.it','lat':46.5635366,'lgt':12.3591123},{'email':'lolitald@libero.it','lat':41.4675671,'lgt':12.9035965},{'email':'gianni58ra@libero.it','lat':43.5493245,'lgt':13.2663479},{'email':'enricomaria.zanni@gmail.com','lat':45.7115434,'lgt':8.907067},{'email':'gloriabaresi@gmail.com','lat':45.406043,'lgt':10.2755113},{'email':'gina.pendusci@gmail.com','lat':46.1492011,'lgt':9.7356576},{'email':'aqa.2011@libero.it','lat':40.2467086,'lgt':18.2719179},{'email':'rybakjoanna@yahoo.it','lat':43.5270861,'lgt':13.2463797},{'email':'lolly6602@yahoo.it','lat':45.5909919,'lgt':9.0782182},{'email':'gianfranco_spada@libero.it','lat':40.5637,'lgt':17.3391743},{'email':'silviasapori.60@gmail.com','lat':44.1645688,'lgt':10.890991},{'email':'lupacchiotta84@gmail.com','lat':43.46328390000001,'lgt':11.8796336}]";
}
