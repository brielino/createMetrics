package packagem;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class OperationDate {

	public static Date convertData(String dateInString) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Logger logger = Logger.getAnonymousLogger();
	    try {
	        return formatter.parse(dateInString);
	    } catch (ParseException e) {
	        logger.info("Errore nel parsiong");
	    }
		return null;
	}
	
	public static List<Date> calcoloDate(String version,String projectName){
		Date dataFile=convertData(takeDataVersion(version,projectName));
		int versionS=Integer.parseInt(version);
		versionS++;
		ArrayList<Date> values= new ArrayList<>();
		String versionSucc=Integer.toString(versionS);
		if(takeDataVersion(versionSucc,projectName).equalsIgnoreCase(".")) {
			values.add(dataFile);
			values.add(null);
		}else {
			values.add(dataFile);
			values.add(convertData(takeDataVersion(versionSucc,projectName)));
		}
		return values;
		
	}
	
	public static String takeDataVersion(String version,String projectName) {
		String csvFile = "C:\\Users\\gabri\\OneDrive\\Desktop\\Bri\\Magistrale Bri\\Secondo Semestre 1\\ISW2\\Falessi\\20200407 Falessi Deliverable 2 Milestone 1 V2\\GetReleaseInfo\\"+projectName+"VersionInfo.csv";
	    BufferedReader br = null;
	    String line = "";
	    String cvsSplitBy = ",";
	    Integer salta=0;
	    String dateVersion =".";
		Logger logger = Logger.getAnonymousLogger();
	    try {

	        br = new BufferedReader(new FileReader(csvFile));
	        while ((line = br.readLine()) != null) {

	            // use comma as separator
	            String[] country = line.split(cvsSplitBy);
	            if(salta != 0 && country[0].equalsIgnoreCase(version) ) {
	            	dateVersion=country[3];
	            	break;
	            }
	            salta++;
	        }

	    } catch (FileNotFoundException e) {
	        logger.info("File non trovato");
	    } catch (IOException e) {
	        logger.info("Errore IO");
	    } finally {
	        if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e) {
	                logger.info("Errore IO 1!");
	            }
	        }
	    }
	    return dateVersion;
	}
}