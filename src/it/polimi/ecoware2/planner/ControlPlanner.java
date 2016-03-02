package it.polimi.ecoware2.planner;

import it.polimi.ecoware2.analyzer.AnalysisReport;
import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.test.utils.Bus;
import it.polimi.ecoware2.test.utils.Commons;

import java.util.Random;


public class ControlPlanner extends Planner
{

	private int step=0;
	private final int MAX_MEM;
	private final int MIM_MEM;
	private final Random random = new Random();

	private final float A1_NOM = 0.1963f;
	private final float A2_NOM = 0.002f;
	private final float A3_NOM = 0.5658f;
	
	private final float P_NOM = 0.4f;
	private final float A = 0.95f;
		
	private float uiOld = 0.0f;

	public ControlPlanner(Allocation minAllocation, Allocation maxAllocation)
	{
		super(minAllocation, maxAllocation);
		MAX_MEM = (int) (maxAllocation.getM()/1E9);
		MIM_MEM = (int)(minAllocation.getM()/1E9);
	}

	
	
	@Override
	public synchronized Allocation nextResourceAllocation()
	{
		step++;
		
		AnalysisReport report = (AnalysisReport) Bus.getShared().get(Commons.ANALYSIS_KEY);
		if(report == null || report.getAvgResponseTime()<=0.0f){
			Bus.getShared().remove(Commons.PLAN_KEY);
			return null;
		}
		
		float rt =  report.getAvgResponseTime();
		float req = report.getRequestNumber();

		float e = Commons.SLA - rt;
		float ke = (A-1)/(P_NOM-1)*e;
		float ui = uiOld+(1-P_NOM)*ke;
		float ut = ui+ke;
		
		
		float core = req*(ut-A1_NOM-1000.0f*A2_NOM)/(1000.0f*A3_NOM*(A1_NOM-ut));
		
		float approxCore = (float) Math.ceil(Math.min(maxAllocation.getC(), Math.max(core, minAllocation.getC())));
		
		float approxUt = ((1000.0f*A2_NOM+A1_NOM)*req+1000.0f*A1_NOM*A3_NOM*approxCore)/(req+1000.0f*A3_NOM*approxCore);
		
		System.out.println("*Control planner, step "+step+"*\nCurrent rt: "+rt+"\nCurrent users: "+req+"\nSLA is set to: "+Commons.SLA+"\nError is: "+e+"\nke is: "+ke+"\nUi, UiOld, Utilde and approxUtilde are: "+ui+" "+uiOld+" "+ut+" "+approxUt+"\nCore and approxCore are: "+core+" "+approxCore+"\n");

		uiOld = approxUt-ke;
		
		Allocation res =  new Allocation((long) (approxCore*1E9), approxCore);
		Bus.getShared().put(Commons.PLAN_KEY, res);
		Bus.getShared().put(Commons.PLAN_UNAPPROX_KEY, new Allocation((long) (((int)core)*1E9), core));

		return res;
		
	}
	

	public long getRandomMem(){
		return (long) ((random.nextInt(MAX_MEM)+MIM_MEM)*1E9);
	}
	
	
	public static void main(String[] args){
		
		
		final float A1 = 0.0933f;
		final float A2 = 0.001f;
		final float A3 = 0.4658f;
		
		final float P_MIN = 0.2f;
		final float P_MAX = 0.7f;
		
		final int N_STEP = 200;
		
		float rt = 2.29f;
		float rtOld = rt;
		
		ControlPlanner cp = new ControlPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION);

		for(int k = 1; k < N_STEP+1; k++){
			
			int req=(int) (500+250*Math.sin(k));
			
			AnalysisReport ar = new AnalysisReport();
			ar.setAvgResponseTime(rt);
			ar.setRequestNumber(req);
			
			Bus.getShared().put(Commons.ANALYSIS_KEY, ar);
			
			cp.nextResourceAllocation();
			
			float core = ((Allocation)Bus.getShared().get(Commons.PLAN_KEY)).getC();
			
			System.out.println("CORE: "+core);
			float p = (float) ((P_MIN+P_MAX)/2+(P_MAX-P_MIN)/2*Math.signum(Math.sin(2*Math.PI*k/N_STEP*4)));
			
			float uf = ((float)core) / req;
			
			float f = (float) (A1+A2/(0.001+A3*uf));
			
			rt = p*rtOld+(1-p)*f;
			System.out.println(p+" "+uf+" "+f+" "+rt+" "+rtOld);

			rtOld = rt;
			
		}
		
	}
	

}
