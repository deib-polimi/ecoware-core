package it.polimi.ecoware2.analyzer;

import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.utils.Commons;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;


public class ContainerProbe
{
	private Stream<Allocation> allocations;
	private Stream<String> appNames;
	
	public ContainerProbe(List<String> appNames){
		this.appNames = appNames.stream();
		refreshCurrentAllocation();
	}
	
	public synchronized void refreshCurrentAllocation(){
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(Commons.EXECUTOR_ALLOCATION_ENDPOINT);
	    try
		{
	    	HttpResponse response = client.execute(request);
			String jsonString = EntityUtils.toString(response.getEntity());	
			JSONObject json = new JSONObject(jsonString);
			
			allocations = appNames.map(a -> {
				float cpu = -1;
				long mem = -1;
				try
				{
					cpu = json.getJSONObject(a).getString("CpusetCpus").split(",").length;
					mem = json.getJSONObject(a).getLong("Memory");
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new Allocation(mem, cpu);
			});
			
		}
		catch (IOException | JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized Map<String, Allocation> getAllocations(){
		Map<String, Allocation> res = new HashMap<String, Allocation>();
		
		List<Allocation> all = allocations.collect(Collectors.toList());
		List<String> names = appNames.collect(Collectors.toList());

		for(int i = 0; i < allocations.count(); i++){
			res.put(names.get(i), all.get(i));
		}
		
		return res;
	}
	
}
