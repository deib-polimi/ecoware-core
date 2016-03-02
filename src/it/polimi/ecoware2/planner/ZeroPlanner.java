package it.polimi.ecoware2.planner;

import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.test.utils.Bus;
import it.polimi.ecoware2.test.utils.Commons;


public class ZeroPlanner extends Planner
{

	public ZeroPlanner(Allocation minAllocation, Allocation maxAllocation)
	{
		super(minAllocation, maxAllocation);
	}

	@Override
	public Allocation nextResourceAllocation()
	{
		Allocation zero = new Allocation(0,0);
		Bus.getShared().put(Commons.PLAN_KEY, zero);
		Bus.getShared().put(Commons.PLAN_UNAPPROX_KEY, zero);

		return zero;
	}

}
