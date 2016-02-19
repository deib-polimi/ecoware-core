package it.polimi.ecoware2.test.utils;


public class Bus extends Report
{
	private static Bus instance;
	
	private Bus(){}
	
	public synchronized static Bus getShared(){
		if(instance==null)
			instance = new Bus();
		return instance;
	}
}
