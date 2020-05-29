package packagem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateFileCsv {
	
	public static final String PERCORSO="C:\\Users\\gabri\\OneDrive\\Desktop\\";
	public static final String COMMIT="commit";
	public static final String AUTHOR="author";
	

	public static List<Integer> takeSha(Date data,Date data1,String projectName) throws IOException, JSONException {
		String token = new String(Files.readAllBytes(Paths.get(PERCORSO+projectName+"Commit.json")));
	    JSONArray object = new JSONArray(token);
		ArrayList<Integer> arraySha= new ArrayList<>();
		for(int i=0;i<object.length() ;i++) {
			String data3=object.getJSONObject(i).getJSONObject(COMMIT).getJSONObject(AUTHOR).getString("date");
			if(data1==null) {
				if(OperationDate.convertData(data3).after(data)){
					arraySha.add(i);
				}else {
					return arraySha;
				}
			}else if(OperationDate.convertData(data3).after(data)) {
					if(OperationDate.convertData(data3).before(data1)) {
						arraySha.add(i);
					}
			}else {
				return arraySha;
			}
		}
		return arraySha;
	}

	public static void writeFile(FileWriter fileWriter,List<String> indiceRelease,BufferedReader reader,JSONArray c,JSONArray object,List<ArrayList<String>>fileBuggy,List<Integer>shaCode) throws JSONException, IOException {
		String line =indiceRelease.get(1);
		for(int j = 0; j < c.length();j++) {
			String[] v=c.getJSONObject(j).getString("path").split("/");
			if(v[v.length-1].contains(".java")) {
				String size=GetMetrics.getSize(line);
				line = reader.readLine();
				ArrayList<String> metriche1=(ArrayList<String>) GetMetrics.calculateMetrics(c.getJSONObject(j).getString("path"),indiceRelease.get(0),(List<Integer>)shaCode,object,(List<ArrayList<String>>)fileBuggy);
				fileWriter.append(indiceRelease.get(0));
				fileWriter.append(",");
				fileWriter.append(c.getJSONObject(j).getString("path"));
				fileWriter.append(",");
				fileWriter.append(metriche1.get(0));
				fileWriter.append(",");
				fileWriter.append(metriche1.get(1));
				fileWriter.append(",");
				fileWriter.append(metriche1.get(2));
				fileWriter.append(",");
				fileWriter.append(metriche1.get(3));
				fileWriter.append(",");
				fileWriter.append(metriche1.get(4));
				fileWriter.append(",");
				fileWriter.append(metriche1.get(5));
				fileWriter.append(",");
				fileWriter.append(metriche1.get(6));
				fileWriter.append(",");
				fileWriter.append(size);
				fileWriter.append(",");
				fileWriter.append(metriche1.get(7));
				fileWriter.append(",");
				fileWriter.append(metriche1.get(8));
				fileWriter.append("\n");
				
			}
		}
	}
	
	public static void main(String[] args) throws IOException, JSONException, InterruptedException {

		String projName ="TAJO";
		ArrayList<ArrayList<String>> ticketBuggy=(ArrayList<ArrayList<String>>) GetMetrics.foundBuggy(projName);
		String token = new String(Files.readAllBytes(Paths.get(PERCORSO+projName+"Commit.json")));
	    JSONArray object = new JSONArray(token);
	    ArrayList<ArrayList<String>> fileBuggy= (ArrayList<ArrayList<String>>) GetMetrics.foundClassBuggy((List<ArrayList<String>>)ticketBuggy,object);
		BufferedReader reader = new BufferedReader(new FileReader(PERCORSO+projName+"Content.txt"));
		String line =reader.readLine();
		ArrayList<String> verVersions= new ArrayList<>();
		Logger logger = Logger.getAnonymousLogger();
		String filePath=PERCORSO+"Metriche"+projName+".csv";
		try(FileWriter fileWriter = new FileWriter(filePath)){
			fileWriter.append("Versione,FileName,LOC_touched,LOC_added,MAX_LOC_Added,AVG_LOC_Added,Churn,MAX_Churn,AVG_Churn,Size,NR,Buggy\n");
			String st1="https://api.github.com/repos/apache/";
			String st2="/tags";
			String st3="?page=";
			int i=0;
			int t=1;
			String nameRelease="";
			String nameRelease1="";
			String indiceRelease="";
			for(;;t++) {
			    JSONArray z =GetConnection.readJsonArrayFromUrl1(st1+projName+st2+st3+t);
				if(z.length()!=0) {
					for(i=0 ; i < z.length() ; i++){
				
						JSONObject f =GetConnection.readJsonFromUrl(z.getJSONObject(i).getJSONObject(COMMIT).getString("url"));
						JSONObject d =GetConnection.readJsonFromUrl(f.getJSONObject(COMMIT).getJSONObject("tree").getString("url")+"?recursive=1");
						JSONArray c=d.getJSONArray("tree");
					    String[] release = z.getJSONObject(i).getString("name").toString().split("-");
					    nameRelease=release[1];
				    	verVersions=(ArrayList<String>)TakeInfoProject.verificsVersion(nameRelease,projName);
					    if(nameRelease.compareTo(nameRelease1)!=0 && !verVersions.isEmpty()) {
					    	ArrayList<Date> datee =null;
					    	indiceRelease=verVersions.get(1);
					    	datee = (ArrayList<Date>) OperationDate.calcoloDate(indiceRelease,projName);
					    	ArrayList<Integer> shaCode=(ArrayList<Integer>) takeSha(datee.get(0),datee.get(1),projName);
					    	nameRelease1=nameRelease;
					    	ArrayList<String> s=new ArrayList<>();
					    	s.add(indiceRelease);
					    	s.add(line);
					    	writeFile(fileWriter,s,reader,c,object,fileBuggy,shaCode);
					    }
					    }
				}else {
					fileWriter.flush();
					break;
				}
			}
	    }catch (IOException e) {
	    	logger.info("Errore");
	    }
	}
}