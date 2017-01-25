package it.polimi.ecoware2.executor;

import it.polimi.ecoware2.utils.Bus;
import it.polimi.ecoware2.utils.Commons;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.amazonaws.util.json.JSONObject;


public class AWSExecutor implements ResourceAllocator
{
	
	private String busKey;
	
	public AWSExecutor(String busKey)
	{
		this.busKey = busKey;
	}
	
	@Override
	public void scheduleNextAllocation()
	{
	
		
	    StringEntity params;
		try
		{
			
			Allocation a = (Allocation) Bus.getShared(busKey).get(Commons.PLAN_KEY);
			if(a == null)
				return;
			
			JSONObject requestJson = new JSONObject();
			JSONObject values = new JSONObject();

			values.put("cpu_cores", (int) a.getC());
			values.put("mem_units", 2*a.getM()/1E9);
			requestJson.put(busKey, values);
		    System.out.println(requestJson);
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(Commons.EXECUTOR_EXECUTE_ENDPOINT);
			params = new StringEntity(requestJson.toString());
			request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    client.execute(request);
		    
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}

}
