package packagem;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TakeInfoProject {

	public static void takeContentClass(String fileUrl,String projectName) throws IOException, JSONException, InterruptedException {
		String st1="https://api.github.com/repos/apache/";
		String st2="/tags";
		String st3="?page=";
		FileWriter file = null;
		int t=1;
		ArrayList<String> allContent = new ArrayList<>();
		for(;;t++){
			JSONArray releaseUrl= GetConnection.readJsonArrayFromUrl1(st1+projectName+st2+st3+t);
			if(releaseUrl.length()!=0) {
				String nameRelease="";
				String nameRelease1="";
				int numberOfRequest=1;
				String currentRelease="";
				for(int i = 0 ; i < releaseUrl.length() ; i++){
					if(numberOfRequest==4500) {
						//System.out.println("Sto Dormendo\n");
						TimeUnit.HOURS.sleep(1);
						numberOfRequest=0;
					}
					String[] release = releaseUrl.getJSONObject(i).getString("name").toString().split("-");
					currentRelease=release[1];
					if(!verificsVersion(currentRelease,projectName).isEmpty()){
						//System.out.println(currentRelease);
						JSONObject releaseActual =GetConnection.readJsonFromUrl(releaseUrl.getJSONObject(i).getJSONObject("commit").getString("url"));
						numberOfRequest++;
						if(numberOfRequest==4500) {
							//System.out.println("Sto Dormendo\n");
							TimeUnit.HOURS.sleep(1);
							numberOfRequest=0;
						}
						numberOfRequest++;
						JSONObject filesF =GetConnection.readJsonFromUrl(releaseActual.getJSONObject("commit").getJSONObject("tree").getString("url")+"?recursive=1");
						JSONArray files=filesF.getJSONArray("tree");
						String[] releaseN = releaseUrl.getJSONObject(i).getString("name").toString().split("-");
					    nameRelease1=releaseN[1];
					
						if(nameRelease1.compareTo(nameRelease)!=0) {
							nameRelease1=nameRelease;
					    	for(int j = 0; j < files.length();j++) {
								String[] v=files.getJSONObject(j).getString("path").split("/");
								if(v[v.length-1].contains(".java")) {
									if(numberOfRequest==4500) {
										//System.out.println("Sto Dormendo\n");
										TimeUnit.HOURS.sleep(1);
										numberOfRequest=0;
									}
									numberOfRequest++;
									JSONObject sizeUrl=GetConnection.readJsonFromUrl(files.getJSONObject(j).getString("url"));
									String content=sizeUrl.getString("content");
									content = content.replaceAll("\r\n|\r|\n","");
									allContent.add(content);
								}
					    	}
					    	//System.out.println(numberOfRequest);
						}
					
					}
				}
			}else {
				break;
			}
		}
		Logger logger = Logger.getAnonymousLogger();
		try { 
	        // Constructs a FileWriter given a file name, using the platform's default charset
	        file = new FileWriter(fileUrl);
	        for(int k = 0; k< allContent.size(); k++) {
	        	file.write(allContent.get(k));
	        	file.write("\n");
	        }

	    } catch (IOException e) {
	        logger.info("Errore IO!");

	    } finally {
	    	file.flush();
	        file.close();
	    }
	}
	
	public static void takeJiraInfo(String pathFile,String projectName) throws IOException, JSONException {
		FileWriter file = null;
		JSONArray json1=new JSONArray();
	    Integer j = 0;
	    Integer i = 0; 
	    Integer total = 1;
	    //Get JSON API for closed bugs w/ AV in the project
	    do {
	       //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
	       j = i + 1000;
	       String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project%20%3D%20"+projectName+"%20AND%20issuetype%20%3D%20Bug%20AND%20status%20in%20(Resolved,%20Closed)%20AND%20resolution%20=%20Fixed%20&&fields=resolutiondate,fixVersions,versions,created&startAt="+ 
	         		i.toString() + "&maxResults=" + j.toString();
	       JSONObject json = GetConnection.readJsonFromUrl1(url);
	       JSONArray issues = json.getJSONArray("issues");
	       total = json.getInt("total");
	       for (; i < total && i < j; i++) {
	          //Iterate through each bug
	          json1.put(issues.getJSONObject(i%1000));
	       }
	    } while (i < total);
	    Logger logger = Logger.getAnonymousLogger();
		try {
			 
	        // Constructs a FileWriter given a file name, using the platform's default charset
	        file = new FileWriter(pathFile);
	        file.write(json1.toString());

	    } catch (IOException e) {
	        logger.info("Errore IO!");

	    } finally {
	    	file.flush();
	        file.close();
	    }
	}
	
	public static void getCommit(String urlFile,String projectName) throws IOException, JSONException {
	    JSONArray object = new JSONArray();
	    FileWriter file = null;
		String url1 = "https://api.github.com/repos/apache/"+projectName+"/commits";
		String url2 = "?page=";
		String url3 = "&per_page=100";
		int i=1;
		for(; ;i++) {
			//System.out.println("Pagina "+i+"\n");
			//Scorro le varie pagine
			JSONArray json = GetConnection.readJsonArrayFromUrl(url1+url2+i+url3);
			Integer l = json.length();
			if(l!= 0) { // Verifico che la pagina contiene almeno un committ
				for(int k = 0 ; k< l ; k++) { // Questo scorre i vari committ
					JSONObject json1 = GetConnection.readJsonFromUrl(url1+"/"+json.getJSONObject(k).getString("sha"));
					object.put(json1);
				}
			}else {
				break;
			}
		}
		Logger logger = Logger.getAnonymousLogger();
		try {
			 
	        // Constructs a FileWriter given a file name, using the platform's default charset
	        file = new FileWriter(urlFile);
	        file.write(object.toString());

	    } catch (IOException e) {
	        logger.info("Errore IO");

	    } finally {
	    	file.flush();
	        file.close();
	    }
	}
	
	public static ArrayList<String> verificsVersion(String version,String projectName) {
		HashMap<String,String> numberVersions= (HashMap<String, String>) GetMetrics.readFileName("C:\\Users\\gabri\\OneDrive\\Desktop\\Bri\\Magistrale Bri\\Secondo Semestre 1\\ISW2\\Falessi\\20200407 Falessi Deliverable 2 Milestone 1 V2\\GetReleaseInfo\\"+projectName+"VersionInfo.csv");
		String corrispondenza="";
		ArrayList<String> verVersion= new ArrayList<>();
		for (String key : numberVersions.keySet()) {
            String value = numberVersions.get(key);
            if(value.compareTo(version)==0) {
            	corrispondenza= ".";
            	verVersion.add(corrispondenza);
            	verVersion.add(key);
            	break;
            }
        }
		return verVersion;		
	}
}