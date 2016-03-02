package it.polimi.ecoware2.executor;


public class Allocation
{
	private final long m;
	private final float c;
	
	
	public Allocation(long m, float f){
		this.m=m;
		this.c=f;
	}

	public long getM()
	{
		return m;
	}

	
	public float getC()
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
