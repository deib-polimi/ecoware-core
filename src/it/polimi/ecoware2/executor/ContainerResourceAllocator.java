package it.polimi.ecoware2.executor;



public abstract class ContainerResourceAllocator implements ResourceAllocator
{
	
	protected String ip;
	protected int port;
	protected String containerId;

	protected Allocation allocation;

	abstract public void scheduleNextAllocation();
	

}
