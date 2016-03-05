package it.polimi.ecoware2.executor;

import it.polimi.ecoware2.test.utils.Bus;
import it.polimi.ecoware2.test.utils.Commons;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.amazonaws.util.json.JSONObject;


public class MonolithicExecutor implements ResourceAllocator
{
	private List<String> busKeys;
	
	
	private ExecutorService executor=Executors.newFixedThreadPool(1);

	public MonolithicExecutor(List<String> busKeys)
	{
		this.busKeys = busKeys;
	}
	
	
	@Override
	public void scheduleNextAllocation()
	{
		executor.execute(() -> {
			_scheduleNextAllocation();
		});
	}
	public void _scheduleNextAllocation()
	{
		
	    StringEntity params;
		try
		{
			
			JSONObject requestJson = new JSONObject();
			
			for(String busKey : busKeys){
				
				Allocation a = (Allocation) Bus.getShared(busKey).get(Commons.PLAN_KEY);
				if(a == null)
					continue;
				
				JSONObject obj = new JSONObject();

				obj.put("cpu_cores", (int) a.getC());
				obj.put("mem_units", 2*a.getM()/1E9);
				requestJson.put(busKey, obj);
			}
			
		
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