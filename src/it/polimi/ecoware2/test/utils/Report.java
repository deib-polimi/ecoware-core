package it.polimi.ecoware2.test.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Report
{
	private Map<String, Object> data = Collections.synchronizedMap(new HashMap<String, Object>());
	
	public Report(){}
	
	public Report(Report report){
		data.putAll(report.data);
	}
	
	public void put(String key, Object value){
		data.put(key, value);
	}
	
	public Object get(String key){
		return data.get(key);
	}
	
	public void remove(String key){
		data.remove(key);
	}
	
}
