package it.polimi.ecoware2.planner;

import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.utils.Bus;
import it.polimi.ecoware2.utils.Commons;


public class MinPlanner extends Planner
{
	public MinPlanner(Allocation minAllocation, Allocation maxAllocation, String busKey)
	{
		super(minAllocation, maxAllocation, busKey);
	}


	@Override
	public Allocation nextResourceAllocation()
	{
		Bus.getShared(busKey).put(Commons.PLAN_KEY, minAllocation);
		Bus.getShared(busKey).put(Commons.PLAN_UNAPPROX_KEY, minAllocation);

		return minAllocation;
	}
	
}