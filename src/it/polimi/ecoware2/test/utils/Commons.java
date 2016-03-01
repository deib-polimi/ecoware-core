package it.polimi.ecoware2.test.utils;

import it.polimi.ecoware2.executor.Allocation;


public class Commons
{
	public static final String MONITORING_KEY = "___mon___";
	public static final String PLAN_KEY = "___plan___";
	public static final String ANALYSIS_KEY = "__analysis__";

	public static final String RT_KEY = "__rt__";
	public static final String REQ_KEY = "__req__";
	public static final String CURRENT_ALLOCATION_KEY = "___ca___";

	public static final String CONTAINER_HOST = "131.175.135.184";
	public static final int CONTAINER_PORT = 2375;
	public static final String SERVER_HOST = "pwitter-lb-1145964028.us-west-2.elb.amazonaws.com";
	public static final int SERVER_PORT = 80;
	public static final String CONTAINER_ID = "0646d0cd73155d57cff79533dfb662d32c06ed881eec898bf8e01198b6a0ce76";
	
	public static final Allocation MAX_ALLOCATION=new Allocation((long) (10*1E9), 10);
	public static final Allocation MIN_ALLOCATION=new Allocation((long) (1*1E9), 1);

	public static final float SLA=0.5f;
	public static final int SAMPLE_TIME = 10;

	public static final String EXECUTOR_EXECUTE_ENDPOINT = "http://localhost:8000/api/executor";
	public static final String SERVER_PATH = "pweets"; //"/rubis/servlet/BrowseRegions";

}
