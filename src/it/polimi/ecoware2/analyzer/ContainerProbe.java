package it.polimi.ecoware2.analyzer;

import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.test.utils.Commons;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;


public class ContainerProbe
{
	private Allocation pwitterAllocation;
	private Allocation rubisAllocation;

	public ContainerProbe(){
		refreshCurrentAllocation();
	}
	
	public synchronized void refreshCurrentAllocation(){
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(Commons.EXECUTOR_ALLOCATION_ENDPOINT);
	    try
		{
	    	/*
			HttpResponse response = client.execute(request);
			String jsonString = EntityUtils.toString(response.getEntity());	
			JSONObject json = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> it = (Iterator<String>) json.keys(); 
			
			float pwitterCpu = 0;
			float rubisCpu = 0;
			
			float pwitterMem = 0;
			float rubisMem = 0;
			
			while(it.hasNext()){
				String key = it.next();
				if(key.contains("i-")){
					JSONObject vmUsed = json.getJSONObject(key).getJSONObject("used");
					pwitterCpu += vmUsed.getJSONObject("pwitter-web").getInt("cpu_cores");
					rubisCpu += vmUsed.getJSONObject("rubis-jboss").getInt("cpu_cores");
					
					pwitterMem += vmUsed.getJSONObject("pwitter-web").getInt("mem_units")/2;
					rubisMem += vmUsed.getJSONObject("rubis-jboss").getInt("mem_units")/2;
					
				}
			}
			*/
	    	
	    	HttpResponse response = client.execute(request);
			String jsonString = EntityUtils.toString(response.getEntity());	
			JSONObject json = new JSONObject(jsonString);
		
			float pwitterCpu = json.getJSONObject("pwitter-web").getString("CpusetCpus").split(",").length;
			float rubisCpu = json.getJSONObject("rubis-jboss").getString("CpusetCpus").split(",").length;
			
			long pwitterMem = json.getJSONObject("pwitter-web").getLong("Memory");
			long rubisMem = json.getJSONObject("rubis-jboss").getLong("Memory");
			
			this.pwitterAllocation = new Allocation(pwitterMem, pwitterCpu);
			this.rubisAllocation = new Allocation(rubisMem, rubisCpu);

			
		}
		catch (IOException | JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized Allocation getPwitterAllocation(){
		return pwitterAllocation;
	}
	
	public synchronized Allocation getRubisAllocation(){
		return rubisAllocation;
	}
	
	
	public static void main(String[] args){
		ContainerProbe p = new ContainerProbe();
		System.out.println(p.getPwitterAllocation());
		System.out.println(p.getRubisAllocation());

	}
}
