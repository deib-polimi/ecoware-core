package it.polimi.ecoware2.test.utils;

import java.util.HashMap;


public class Bus extends Report
{
	private static HashMap<String, Bus> instances=new HashMap<String, Bus>();
	
	private Bus(){}
	
	public synchronized static Bus getShared(String key){
		Bus instance = instances.get(key);
		if(instance==null){
			instance = new Bus();
			instances.put(key, instance);
		}
		return instance;
	}
}
