package it.polimi.ecoware2.executor;

import it.polimi.ecoware2.test.utils.Bus;
import it.polimi.ecoware2.test.utils.Commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class DockerResourceAllocator extends ContainerResourceAllocator
{

	private DockerRestClient client;

	public DockerResourceAllocator(String ip, int port, String containerId){
		this.ip = ip;
		this.port = port;
		this.containerId = containerId;
		client = new DockerRestClient();
		this.allocation=client.getCurrentResources();
		Bus.getShared().put(Commons.CURRENT_ALLOCATION_KEY, this.allocation);
		System.out.println("Starting allocation "+allocation);
	}

	@Override
	public void scheduleNextAllocation()
	{
		
		Allocation a = (Allocation) Bus.getShared().get(Commons.PLAN_KEY);
		
		if(a==null){
			System.out.println("No allocation planned this step");
			return;
		}
		
		if(a.getC()<1){
			System.out.println("Allocation failed due to invalid cpu input");
			return;
		}

		if(a.getM()<(512*1E6)){
			System.out.println("Allocation failed due to invalid memory input");
			return;
		}

		System.out.println("Starting allocation of "+a);

		boolean r = client.setResources(a);
		if(r){
			this.allocation = a;
			Bus.getShared().put(Commons.CURRENT_ALLOCATION_KEY, a);
			System.out.println("New resources allocated "+a);
		}
		else
			System.out.println("Allocation failed due to internal failure");

	}

	private class DockerRestClient{

		private JsonParser parser = new JsonParser();

		public Allocation getCurrentResources(){
			HttpClient rest = HttpClientBuilder.create().build();
			HttpGet getRequest = new HttpGet(
					"http://"+ip+":"+port+"/containers/"+containerId+"/json");
			getRequest.addHeader("accept", "application/json");

			try
			{
				HttpResponse response = rest.execute(getRequest);
				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));

				String res=br.readLine();
				JsonObject o = (JsonObject)parser.parse(res);

				String[] cpuString = o.getAsJsonObject("HostConfig").get("CpusetCpus").getAsString().split("-");
				int cpu;
				if(cpuString.length!=2){
					cpu=1;
				}
				else
					cpu = Integer.parseInt(cpuString[1])+1;

				long mem=o.getAsJsonObject("HostConfig").get("Memory").getAsLong();
				return new Allocation(mem, cpu);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return null;
		}

		public boolean setResources(Allocation a){
			String cpu; 
			if(a.getC()==1)
				cpu = "0";
			else
				cpu = "0-"+(a.getC()-1);

			setResources(a.getM(), cpu);

			return a.equals(getCurrentResources());
		}

		protected void setResources(long memory, String cpu){
			HttpClient rest = HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost("http://"+ip+":"+port+"/containers/"+containerId+"/set");
			StringEntity input;
			try
			{

				input = new StringEntity("{\"memory\" : "+String.valueOf(memory)+", \"cpusetcpus\" : \""+cpu+"\"}");
				input.setContentType("application/json");
				postRequest.setEntity(input);

				HttpResponse response=rest.execute(postRequest);
				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));
				String res=br.readLine();
				System.out.println(res);

			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			catch (ClientProtocolException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	
		}


	}

}
