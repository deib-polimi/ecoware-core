package it.polimi.ecoware2.test.main;

import it.polimi.ecoware2.analyzer.JMeterProbeAndAnalyzer;
import it.polimi.ecoware2.executor.DockerResourceAllocator;
import it.polimi.ecoware2.executor.ResourceAllocator;
import it.polimi.ecoware2.planner.ControlPlanner;
import it.polimi.ecoware2.planner.Planner;
import it.polimi.ecoware2.planner.RandomPlanner;
import it.polimi.ecoware2.test.jmeter.JMeterSetup;
import it.polimi.ecoware2.test.utils.Commons;



public class ECoWareMain
{
	private static ResourceAllocator executor;
	private static Planner planner;
	private static JMeterSetup jmeter;

	private static Integer[] userPerStep = {50, 10};
	private static int step = 3;
	private static int numStep;
	private static int resChange = 10;
	private static boolean end = false;
	
	public static void main(String[] args) throws Exception {

		if(args!=null && args.length>2){
			step=Integer.parseInt(args[0]);
			resChange=Integer.parseInt(args[1]);
			
			Integer[] steps=new Integer[args.length-2];
			for(int i=2; i<args.length; i++){
					steps[i-2]=Integer.parseInt(args[i]);
			}
			
			userPerStep = steps;
		}
		
		String fileName = "testleva-"+System.currentTimeMillis()+"--"+arrayToString(userPerStep)+"_"+step+"_"+numStep+"_"+resChange+".csv";
	
		System.out.println("New JMeter test is going to be launched");
		System.out.println("Resource will change every "+resChange+" seconds");
		System.out.println("Saving output on file "+fileName);

		numStep = userPerStep.length;
		
		executor = new DockerResourceAllocator(Commons.CONTAINER_HOST, Commons.CONTAINER_PORT, Commons.CONTAINER_ID);
		planner = new ControlPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION);
		
		Planner startingPlanner = new RandomPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION);
		startingPlanner.nextResourceAllocation();
		executor.scheduleNextAllocation();
		
		jmeter=new JMeterSetup();
		jmeter.setCollector(new JMeterProbeAndAnalyzer(fileName, () -> { 
			System.out.println("end callback");
			end = true; }));
		
		jmeter.startTestWith(step, userPerStep);
		
		
		new Thread(() -> {
			int cont=0;
				while(!end){
					try
					{
						Thread.sleep(1000);
						cont+=1000;
						if(cont>(resChange*1000)){
							cont=0;
							planner.nextResourceAllocation();
							executor.scheduleNextAllocation();
						}
						
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				System.out.println("End planning iteration");
		}).start();
		
	}
	
	static String arrayToString(Integer[] array)
	{
	
		String res=array[0]+"";
		for(int i=1; i<array.length; i++){
			res+=","+array[i];
		}
			
		return res;
	}
}
