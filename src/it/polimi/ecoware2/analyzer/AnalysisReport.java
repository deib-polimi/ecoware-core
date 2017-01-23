package it.polimi.ecoware2.analyzer;

import it.polimi.ecoware2.utils.Commons;
import it.polimi.ecoware2.utils.Report;


public class AnalysisReport extends Report
{
	public void setAvgResponseTime(float rt){
		this.put(Commons.RT_KEY, rt);
	}
	
	public void setRequestNumber(int req){
		this.put(Commons.REQ_KEY, req);
	}
	
	public float getAvgResponseTime(){
		return (float) this.get(Commons.RT_KEY);
	}
	
	public int getRequestNumber(){
		return (int) this.get(Commons.REQ_KEY);
	}
}
