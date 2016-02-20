package it.polimi.ecoware2.planner;

import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.test.utils.Bus;
import it.polimi.ecoware2.test.utils.Commons;


public class MinPlanner extends Planner
{
	public MinPlanner(Allocation minAllocation, Allocation maxAllocation)
	{
		super(minAllocation, maxAllocation);
	}


	@Override
	public Allocation nextResourceAllocation()
	{
		Bus.getShared().put(Commons.PLAN_KEY, minAllocation);
		return minAllocation;
	}
	
}