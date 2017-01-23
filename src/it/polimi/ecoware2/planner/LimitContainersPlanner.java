package it.polimi.ecoware2.planner;

import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.utils.Bus;
import it.polimi.ecoware2.utils.Commons;


public class LimitContainersPlanner extends Planner
{
	private String firstBusKey;
	private String secondBusKey;

	public LimitContainersPlanner(Allocation minAllocation, Allocation maxAllocation, String firstBusKey, String secondBusKey)
	{
		super(minAllocation, maxAllocation, null);
		this.firstBusKey = firstBusKey;
		this.secondBusKey = secondBusKey;
	}

	@Override
	public Allocation nextResourceAllocation()
	{
		Allocation a1 = (Allocation) Bus.getShared(firstBusKey).get(Commons.PLAN_KEY);
		Allocation a2 = (Allocation) Bus.getShared(secondBusKey).get(Commons.PLAN_KEY);


		Allocation a1N = a1;
		Allocation a2N = a2;

		if(a1.getC()+a2.getC()>maxAllocation.getC()){
			if(a1.getC() > maxAllocation.getC()/2 && a2.getC() > maxAllocation.getC()/2){
				a1N = new Allocation((long) (1E9*maxAllocation.getC()/2), maxAllocation.getC()/2);
				a2N = new Allocation((long) (1E9*maxAllocation.getC()/2), maxAllocation.getC()/2);
			}
			else if(a1.getC() > maxAllocation.getC()/2 && a2.getC() <= maxAllocation.getC()/2){
				a1N = new Allocation((long) (1E9*(maxAllocation.getC()-a2.getC())), maxAllocation.getC()-a2.getC());
			}
			else if(a1.getC() <= maxAllocation.getC()/2 && a2.getC() > maxAllocation.getC()/2){
				a2N = new Allocation((long) (1E9*(maxAllocation.getC()-a1.getC())), maxAllocation.getC()-a1.getC());
			}
		}
		
		System.out.println("Limiting "+firstBusKey+" to "+a1N);
		System.out.println("Limiting "+secondBusKey+" to "+a2N);

		Bus.getShared(firstBusKey).put(Commons.PLAN_KEY, a1N);
		Bus.getShared(secondBusKey).put(Commons.PLAN_KEY, a2N);

		return null;
	}
	
	public static void main(String[] args){
		
		LimitContainersPlanner p = new LimitContainersPlanner(null, new Allocation(1, 8), "a", "b");
		Bus.getShared("a").put(Commons.PLAN_KEY, new Allocation((long) (1*1E9), 5));
		Bus.getShared("b").put(Commons.PLAN_KEY, new Allocation((long) (1*1E9), 3));
		p.nextResourceAllocation();
	}

}
