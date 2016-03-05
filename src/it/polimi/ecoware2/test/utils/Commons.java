package it.polimi.ecoware2.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import it.polimi.ecoware2.executor.Allocation;


public class Commons
{
	public static final String MONITORING_KEY = "___mon___";
	public static final String PLAN_KEY = "___plan___";
	public static final String PLAN_UNAPPROX_KEY = "___unapprox_plan___";
	public static final String ANALYSIS_KEY = "__analysis__";

	public static final String RT_KEY = "__rt__";
	public static final String REQ_KEY = "__req__";
	public static final String CURRENT_ALLOCATION_KEY = "___ca___";
	public static float CONTROLLER_ALPHA;
	
	
	public static String CONTAINER_HOST;// = "131.175.135.184";
	public static int CONTAINER_PORT;// = 2375;
	public static  String SERVER_HOST; //= "pwitter-lb-1145964028.us-west-2.elb.amazonaws.com";
	public static  int SERVER_PORT;// = 80;
	public static  String CONTAINER_ID;// = "0646d0cd73155d57cff79533dfb662d32c06ed881eec898bf8e01198b6a0ce76";
	
	public static  Allocation MAX_ALLOCATION;//=new Allocation((long) (10*1E9), 10);
	public static  Allocation MIN_ALLOCATION;//=new Allocation((long) (1*1E9), 1);

	public static  float SLA;//=0.5f;
	public static  int SAMPLE_TIME;// = 30;

	public static  String EXECUTOR_EXECUTE_ENDPOINT;// = "http://localhost:8000/api/executor";
	public static  String EXECUTOR_ALLOCATION_ENDPOINT;

	public static  String SERVER_PATH;// = "pweets"; //"/rubis/servlet/BrowseRegions";
	
	public static  String AWS_SCALE_GROUP;// = "pwitter-web";
	
	static {
		
		try {
			File file = new File("ecoware.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			Enumeration<?> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				System.out.println(key + ": " + value);
			}
			
			CONTAINER_HOST = properties.getProperty("CONTAINER_HOST");
			CONTAINER_PORT = Integer.parseInt(properties.getProperty("CONTAINER_PORT"));
			SERVER_HOST = properties.getProperty("SERVER_HOST");
			SERVER_PORT = Integer.parseInt(properties.getProperty("SERVER_PORT"));
			MAX_ALLOCATION = new Allocation((long) (Integer.parseInt(properties.getProperty("MAX_ALLOCATION_MEM"))*1E9), Integer.parseInt(properties.getProperty("MAX_ALLOCATION_CORE")));
			MIN_ALLOCATION = new Allocation((long) (Integer.parseInt(properties.getProperty("MIN_ALLOCATION_MEM"))*1E9), Integer.parseInt(properties.getProperty("MIN_ALLOCATION_CORE")));
			SLA = Float.parseFloat(properties.getProperty("SLA"));
			SAMPLE_TIME = Integer.parseInt(properties.getProperty("SAMPLE_TIME"));
			EXECUTOR_EXECUTE_ENDPOINT = properties.getProperty("EXECUTOR_EXECUTE_ENDPOINT");
			SERVER_PATH = properties.getProperty("SERVER_PATH");
			AWS_SCALE_GROUP = properties.getProperty("AWS_SCALE_GROUP");
			CONTROLLER_ALPHA = Float.parseFloat(properties.getProperty("CONTROLLER_ALPHA"));

						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}
