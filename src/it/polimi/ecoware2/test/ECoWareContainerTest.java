package it.polimi.ecoware2.test;

import it.polimi.ecoware2.analyzer.ContainerProbe;
import it.polimi.ecoware2.executor.MonolithicExecutor;
import it.polimi.ecoware2.executor.ResourceAllocator;
import it.polimi.ecoware2.planner.ControlPlanner;
import it.polimi.ecoware2.planner.MinPlanner;
import it.polimi.ecoware2.planner.Planner;
import it.polimi.ecoware2.utils.Bus;
import it.polimi.ecoware2.utils.Commons;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ECoWareContainerTest extends Test
{
	private ContainerProbe probe;
	private ResourceAllocator executor;
	private Stream<Planner> planners;
	
	private Stream<TestInfo> testInfos;
	
	public ECoWareContainerTest(List<File> files){
		testInfos = files.stream().map(a -> new TestInfo(a));
	}
	
	@Override
	public void start() {
		
		executor = new MonolithicExecutor(testInfos.map(a -> a.name).collect(Collectors.toList()));
		planners = testInfos.map(a -> new ControlPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION, Commons.CONTROLLER_ALPHA, a.name));
		
		Stream<Planner> initialPlanners = testInfos.map(a -> new MinPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION, a.name));
		initialPlanners.forEach(a -> a.nextResourceAllocation());
		
		executor.scheduleNextAllocation();
		
		probe = new ContainerProbe(testInfos.map(a -> a.name).collect(Collectors.toList()));
		
		probe.getAllocations().entrySet().forEach(a -> {
			Bus.getShared(a.getKey()).put(Commons.CURRENT_ALLOCATION_KEY, a.getValue());
		});
		
		testInfos.forEach(a -> this.startTest(a, a.name));
		
	}
	


	@Override
	protected void refreshAllocation()
	{
		probe.refreshCurrentAllocation();
		probe.getAllocations().entrySet().forEach(a -> {
			Bus.getShared(a.getKey()).put(Commons.CURRENT_ALLOCATION_KEY, a.getValue());
		});		
	}

	@Override
	protected void allocateAndExecute()
	{
		planners.forEach(a -> a.nextResourceAllocation());
		executor.scheduleNextAllocation();
		probe.refreshCurrentAllocation();	
	}
}
