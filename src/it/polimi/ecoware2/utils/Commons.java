package it.polimi.ecoware2.utils;

import it.polimi.ecoware2.executor.Allocation;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;


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
	
	public static  Allocation MAX_ALLOCATION;//=new Allocation((long) (10*1E9), 10);
	public static  Allocation MIN_ALLOCATION;//=new Allocation((long) (1*1E9), 1);

	public static  float SLA; //=0.5f;
	public static  int SAMPLE_TIME;// = 30;
	public static  int CONTROL_PERIOD;
	public static  String EXECUTOR_EXECUTE_ENDPOINT;// = "http://localhost:8000/api/executor";
	public static  String EXECUTOR_ALLOCATION_ENDPOINT;
	
	public static  String AWS_SCALE_GROUP;// = "pwitter-web";
	
	public static  String AWS_ACCESS_KEY;
	
	public static  String AWS_SECRET_KEY;

	public static  String AWS_REGION;

	

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
		
			MAX_ALLOCATION = new Allocation((long) (Integer.parseInt(properties.getProperty("MAX_ALLOCATION_MEM"))*1E9), Integer.parseInt(properties.getProperty("MAX_ALLOCATION_CORE")));
			MIN_ALLOCATION = new Allocation((long) (Integer.parseInt(properties.getProperty("MIN_ALLOCATION_MEM"))*1E9), Integer.parseInt(properties.getProperty("MIN_ALLOCATION_CORE")));
			SLA = Float.parseFloat(properties.getProperty("SLA"));
			SAMPLE_TIME = Integer.parseInt(properties.getProperty("SAMPLE_TIME"));
			CONTROL_PERIOD = Integer.parseInt(properties.getProperty("CONTROL_PERIOD"));

			EXECUTOR_EXECUTE_ENDPOINT = properties.getProperty("EXECUTOR_EXECUTE_ENDPOINT");
			EXECUTOR_ALLOCATION_ENDPOINT = properties.getProperty("EXECUTOR_ALLOCATION_ENDPOINT");
		
			AWS_SCALE_GROUP = properties.getProperty("AWS_SCALE_GROUP");
		
			CONTROLLER_ALPHA = Float.parseFloat(properties.getProperty("CONTROLLER_ALPHA"));
			AWS_ACCESS_KEY = properties.getProperty("AWS_ACCESS_KEY");
			AWS_SECRET_KEY = properties.getProperty("AWS_SECRET_KEY");
			AWS_REGION = properties.getProperty("AWS_REGION");
			
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}
