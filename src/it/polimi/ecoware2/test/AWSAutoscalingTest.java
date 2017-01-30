package it.polimi.ecoware2.test;

import java.io.File;

import it.polimi.ecoware2.analyzer.AWSProbe;
import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.utils.Bus;
import it.polimi.ecoware2.utils.Commons;

import com.amazonaws.regions.Regions;



public class AWSAutoscalingTest extends Test
{
	private TestInfo testInfo;
	private String busKey;
	
	private static AWSProbe probe;
	
	public AWSAutoscalingTest(File properties){
		testInfo = new TestInfo(properties);
		busKey = testInfo.name;
	}
	
	
	@Override
	public void start() {
		probe = new AWSProbe(new Allocation(Commons.AWS_VM_FLAVOR_MEM*(long)1E9, Commons.AWS_VM_FLAVOR_CPU), Commons.AWS_ACCESS_KEY, Commons.AWS_SECRET_KEY, Regions.valueOf(Commons.AWS_REGION), Commons.AWS_SCALE_GROUP);
		Bus.getShared(busKey).put(Commons.CURRENT_ALLOCATION_KEY, probe.getCurrentAllocation());
		
		startAllocationMonitoringLoop();
		
		startTest(testInfo, busKey);
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
