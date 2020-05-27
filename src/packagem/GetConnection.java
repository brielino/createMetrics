package packagem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetConnection {

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		URLConnection uc = takeUrlConnection(url); 
		InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
		try {
		    BufferedReader rd = new BufferedReader(inputStreamReader);
		    JSONObject  jsonObject = new JSONObject(readAll(rd));
		    return jsonObject;
		} finally {
		      inputStreamReader.close();
		}
		   
	}
	
	public static JSONObject readJsonFromUrl1(String url) throws IOException, JSONException {
		   InputStream is = new URL(url).openStream();
		   try {
			   BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			   String jsonText = readAll(rd);
			   JSONObject json = new JSONObject(jsonText);
			   return json;
		   } finally {
			   is.close();
		   }
	}
	
	private static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	}
	public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	       BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
	       String jsonText = readAll(rd);
	       JSONArray json = new JSONArray(jsonText);
	       return json;
	     } finally {
	       is.close();
	     }
	}
	public static JSONArray readJsonArrayFromUrl1(String url) throws IOException, JSONException {
		//Utilizzato per fare richieste autenticate che permette di fare 5000 richieste l'ora
        URLConnection uc = takeUrlConnection(url);

		InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
		try {
		    BufferedReader rd = new BufferedReader(inputStreamReader);
		    JSONArray  jsonObject = new JSONArray(readAll(rd));
		    return jsonObject;
		} finally {
		      inputStreamReader.close();
		}
		   
	}
	public static URLConnection takeUrlConnection(String url) throws IOException {
		URL url_1 = new URL(url);
        URLConnection uc = url_1.openConnection();
        uc.setRequestProperty("X-Requested-With", "Curl");
        String username =  "Brielino";
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\gabri\\OneDrive\\Desktop\\Token richieste autorizzate GitHub.txt"));
        String line = reader.readLine();
        String token =  line;
        String userpass = username + ":" + token;
        byte[] encodedBytes = Base64.getEncoder().encode(userpass.getBytes());
        String basicAuth = "Basic " + new String(encodedBytes);
        uc.setRequestProperty("Authorization", basicAuth);
        return uc;
	}
}