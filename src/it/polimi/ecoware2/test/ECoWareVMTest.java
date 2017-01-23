package it.polimi.ecoware2.test;

import it.polimi.ecoware2.analyzer.AWSProbe;
import it.polimi.ecoware2.executor.AWSExecutor;
import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.executor.ResourceAllocator;
import it.polimi.ecoware2.planner.ControlPlanner;
import it.polimi.ecoware2.planner.MinPlanner;
import it.polimi.ecoware2.planner.Planner;
import it.polimi.ecoware2.utils.Bus;
import it.polimi.ecoware2.utils.Commons;

import com.amazonaws.regions.Regions;



public class ECoWareVMTest extends Test
{
	private static AWSProbe probe;
	private static ResourceAllocator executor;
	private static Planner planner;
	
	private static String busKey = "ecoware-vm-experiment";

	@Override
	public void start()
	{
		executor = new AWSExecutor(busKey);
		planner = new ControlPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION, busKey);

		probe = new AWSProbe(new Allocation(2*(long)1E9, 1), Commons.AWS_ACCESS_KEY, Commons.AWS_SECRET_KEY, Regions.valueOf(Commons.AWS_REGION), Commons.AWS_SCALE_GROUP);
		forceInitialAllocation();
		Bus.getShared(busKey).put(Commons.CURRENT_ALLOCATION_KEY, probe.getCurrentAllocation());

		startAllocationMonitoringLoop();

		startPlanningAndExecutionMonitoringLoop();
		
		startJMeter(busKey);		
	}

	private static void forceInitialAllocation() {
		Planner startingPlanner = new MinPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION, busKey);
		Allocation initialAlloc=startingPlanner.nextResourceAllocation();
		executor.scheduleNextAllocation();
		while(probe.getCurrentAllocation().getC() != initialAlloc.getC()){
			System.out.println(probe.getCurrentAllocation());
			System.out.println(initialAlloc);
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			probe.fetchAllocationFromAWS();
		}
	}


	@Override
	protected void refreshAllocation() {
		probe.fetchAllocationFromAWS();
		Bus.getShared(busKey).put(Commons.CURRENT_ALLOCATION_KEY, probe.getCurrentAllocation());
		System.out.println(probe.getCurrentAllocation());
	}


	@Override
	protected void allocateAndExecute() {
		planner.nextResourceAllocation();
		executor.scheduleNextAllocation();
		probe.fetchAllocationFromAWS();		
	}

}
