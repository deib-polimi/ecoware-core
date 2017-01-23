package it.polimi.ecoware2.test;

import it.polimi.ecoware2.analyzer.AWSProbe;
import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.utils.Bus;
import it.polimi.ecoware2.utils.Commons;

import com.amazonaws.regions.Regions;



public class AWSAutoscalingTest extends Test
{

	private static AWSProbe probe;
	private static String busKey = "aws-autoscaling-experiment";

	@Override
	public void start() {

		probe = new AWSProbe(new Allocation(2*(long)1E9, 1), Commons.AWS_ACCESS_KEY, Commons.AWS_SECRET_KEY, Regions.valueOf(Commons.AWS_REGION), Commons.AWS_SCALE_GROUP);
		Bus.getShared(busKey).put(Commons.CURRENT_ALLOCATION_KEY, probe.getCurrentAllocation());
		
		startAllocationMonitoringLoop();
		
		startJMeter(busKey);
	}
	
	@Override
	protected void refreshAllocation(){
		probe.fetchAllocationFromAWS();
		Bus.getShared(busKey).put(Commons.CURRENT_ALLOCATION_KEY, probe.getCurrentAllocation());
		System.out.println(probe.getCurrentAllocation());
	}


	@Override
	protected void allocateAndExecute(){}
	

	
}
