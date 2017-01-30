package it.polimi.ecoware2.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public class TestInfo
{
	public final  String name;

	public final  int stepDuration;
		
	public final  List<Integer> steps;
	
	public final  String serverHost; //= "pwitter-lb-1145964028.us-west-2.elb.amazonaws.com";
	public final  int serverPort;// = 80;
	
	public final  String serverPath;// = "pweets"; //"/rubis/servlet/BrowseRegions";
	
	
	public TestInfo(File propertiesFile){
		Properties properties = new Properties();
		try
		{
			FileInputStream fileInput = new FileInputStream(propertiesFile);
			properties.load(fileInput);
			fileInput.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		name = properties.getProperty("TEST_NAME");
		stepDuration = Integer.parseInt(properties.getProperty("STEP_DURATION"));
		steps = Arrays.asList(properties.getProperty("STEPS").split(",")).stream().map(a -> Integer.parseInt(a)).collect(Collectors.toList());
		serverHost = properties.getProperty("SERVER_HOST");
		serverPath = properties.getProperty("SERVER_PATH");
		serverPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));
		
		
	}

}
