package it.polimi.ecoware2.planner;

import it.polimi.ecoware2.executor.Allocation;


public abstract class Planner
{
	protected Allocation maxAllocation;
	protected Allocation minAllocation;
		
	public Planner(Allocation minAllocation, Allocation maxAllocation)
	{
		setMinAllocation(minAllocation);
		setMaxAllocation(maxAllocation);
	}

	public void setMaxAllocation(Allocation maxAllocation){
		this.maxAllocation=maxAllocation;
	}
	
	public void setMinAllocation(Allocation minAllocation){
		this.minAllocation=minAllocation;
	}
	
	public abstract Allocation nextResourceAllocation();
}
