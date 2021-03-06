package it.polimi.ecoware2.planner;

import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.utils.Bus;
import it.polimi.ecoware2.utils.Commons;

import java.util.Random;


public class RandomPlanner extends Planner
{
	public RandomPlanner(Allocation minAllocation, Allocation maxAllocation, String busKey)
	{
		super(minAllocation, maxAllocation, busKey);
	}

	private Random r = new Random();

	@Override
	public Allocation nextResourceAllocation()
	{

		int maxM = (int) (maxAllocation.getM()/1E9);
		int minM = (int)(minAllocation.getM()/1E9);
		long m = (long) ((r.nextInt(maxM)+minM)*1E9);
		float mc = maxAllocation.getC();
		float c = r.nextInt((int)mc)+minAllocation.getC();

		Allocation res = new Allocation(m, c);

		Bus.getShared(busKey).put(Commons.PLAN_KEY, res);
		Bus.getShared(busKey).put(Commons.PLAN_UNAPPROX_KEY, res);

		return res;

	}

}
