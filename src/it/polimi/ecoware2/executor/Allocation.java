package it.polimi.ecoware2.executor;


public class Allocation
{
	private final long m;
	private final int c;
	
	
	public Allocation(long m, int c){
		this.m=m;
		this.c=c;
	}

	public long getM()
	{
		return m;
	}

	
	public int getC()
	{
		return c;
	}

	@Override
	public String toString(){
		return "("+m/1E9+"GB, "+c+" cores)";
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof Allocation){
			Allocation a = (Allocation)obj;
			return a.m == m && a.c == c;
		}
		return false;
	}
}
