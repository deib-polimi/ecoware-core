package it.polimi.ecoware2.planner;

import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.utils.Bus;
import it.polimi.ecoware2.utils.Commons;


public class ZeroPlanner extends Planner
{

	public ZeroPlanner(Allocation minAllocation, Allocation maxAllocation, String busKey)
	{
		super(minAllocation, maxAllocation, busKey);
	}

	@Override
	public Allocation nextResourceAllocation()
	{
		Allocation zero = new Allocation(0,0);
		Bus.getShared(busKey).put(Commons.PLAN_KEY, zero);
		Bus.getShared(busKey).put(Commons.PLAN_UNAPPROX_KEY, zero);

		return zero;
	}

}
