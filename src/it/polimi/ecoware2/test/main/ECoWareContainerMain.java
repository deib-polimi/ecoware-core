package it.polimi.ecoware2.test.main;

import it.polimi.ecoware2.analyzer.ContainerProbe;
import it.polimi.ecoware2.analyzer.JMeterProbeAndAnalyzer;
import it.polimi.ecoware2.executor.MonolithicExecutor;
import it.polimi.ecoware2.executor.ResourceAllocator;
import it.polimi.ecoware2.planner.ControlPlanner;
import it.polimi.ecoware2.planner.LimitContainersPlanner;
import it.polimi.ecoware2.planner.MinPlanner;
import it.polimi.ecoware2.planner.Planner;
import it.polimi.ecoware2.test.jmeter.JMeterSetup;
import it.polimi.ecoware2.test.utils.Bus;
import it.polimi.ecoware2.test.utils.Commons;

import java.util.Arrays;


public class ECoWareContainerMain
{
	private static ContainerProbe probe;
	private static ResourceAllocator executor;
	private static Planner rubisPlanner;
	private static Planner pwitterPlanner;

	private static JMeterSetup rubisJmeter;
	private static JMeterSetup pwitterJmeter;

	private static Integer[] rubisUserPerStep = {};
	private static Integer[] pwitterUserPerStep = {};

	private static int step = 3;
	private static int numStep = 5;
	private static int resChange = 10;
	private static boolean end = false;
	
	public static void main(String[] args) throws Exception {
		
		//String awsAccessKey = args[0];
		//String awsSecretKey = args[1];
		String name = args[2];
		
		if(args!=null && args.length>3){
			step=Integer.parseInt(args[3]);
			resChange=Integer.parseInt(args[4]);
			
			
			Integer[] steps1=new Integer[numStep];
			for(int i=5; i<5+numStep; i++){
					steps1[i-5]=Integer.parseInt(args[i]);
			}
		
			rubisUserPerStep = steps1;
			
			Integer[] steps2=new Integer[numStep];
			for(int i=5+numStep; i<10+numStep; i++){
					steps2[i-(5+numStep)]=Integer.parseInt(args[i]);
			}
		
			pwitterUserPerStep = steps2;
			
			
		}
		
		
		String rubisFileName = "rubis-"+name+"-"+System.currentTimeMillis()+"--"+arrayToString(rubisUserPerStep)+"_"+step+"_"+numStep+"_"+resChange+".csv";
		String pwitterFileName = "pwitter-"+name+"-"+System.currentTimeMillis()+"--"+arrayToString(pwitterUserPerStep)+"_"+step+"_"+numStep+"_"+resChange+".csv";

		String rubisBusKey = "rubis-jboss";
		String pwitterBusKey = "pwitter-web";

		System.out.println("New JMeter test is going to be launched");
		System.out.println("Resource will change every "+resChange+" seconds");
		
		executor = new MonolithicExecutor(Arrays.asList(rubisBusKey, pwitterBusKey));
		rubisPlanner = new ControlPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION, rubisBusKey);
		pwitterPlanner = new ControlPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION, pwitterBusKey);

		Planner rubisStartingPlanner = new MinPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION, rubisBusKey);
		Planner pwitterStartingPlanner = new MinPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION, pwitterBusKey);

		rubisStartingPlanner.nextResourceAllocation();
		pwitterStartingPlanner.nextResourceAllocation();
		executor.scheduleNextAllocation();
		
		rubisJmeter=new JMeterSetup();
		rubisJmeter.setCollector(new JMeterProbeAndAnalyzer(rubisFileName, () -> { 
			System.out.println("end callback");
			end = true; }, rubisBusKey));
		
		pwitterJmeter=new JMeterSetup();
		pwitterJmeter.setCollector(new JMeterProbeAndAnalyzer(pwitterFileName, () -> { 
			System.out.println("end callback");
			end = true; }, pwitterBusKey));
		
		probe = new ContainerProbe();
		Bus.getShared(rubisBusKey).put(Commons.CURRENT_ALLOCATION_KEY, probe.getRubisAllocation());
		Bus.getShared(pwitterBusKey).put(Commons.CURRENT_ALLOCATION_KEY, probe.getPwitterAllocation());

		/*while(probe.getCurrentAllocation().getC() != initialAlloc.getC()){
			System.out.println(probe.getCurrentAllocation());
			System.out.println(initialAlloc);
			Thread.sleep(1000);
			probe.refreshCurrentAllocation();
		}
		*/
		
		new Thread(() -> {
			int cont=0;
				while(!end){
					try
					{
						Thread.sleep(1000);
						cont+=1;
						if(cont % 10 == 0){
							cont=0;
							probe.refreshCurrentAllocation();
							Bus.getShared(pwitterBusKey).put(Commons.CURRENT_ALLOCATION_KEY, probe.getPwitterAllocation());
							Bus.getShared(rubisBusKey).put(Commons.CURRENT_ALLOCATION_KEY, probe.getRubisAllocation());

							System.out.println("Pwitter\n"+probe.getPwitterAllocation());
							System.out.println("Rubis\n"+probe.getRubisAllocation());


						}
						
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				System.out.println("End planning iteration");
		}).start();
		
		rubisJmeter.startTestWith(step, rubisUserPerStep, Commons.SERVER_HOST, 80, "/rubis/servlet/BrowseRegions");
		pwitterJmeter.startTestWith(step, pwitterUserPerStep, Commons.SERVER_HOST, 8080, "pweets");

		new Thread(() -> {
			
			LimitContainersPlanner lPlanner = new LimitContainersPlanner(Commons.MIN_ALLOCATION, Commons.MAX_ALLOCATION, rubisBusKey, pwitterBusKey);
			int cont=0;
				while(!end){
					try
					{
						Thread.sleep(1000);
						cont+=1;
						if(cont>(resChange)){
							cont=0;
							rubisPlanner.nextResourceAllocation();
							pwitterPlanner.nextResourceAllocation();
							lPlanner.nextResourceAllocation();
							executor.scheduleNextAllocation();
							probe.refreshCurrentAllocation();	
							System.out.println("Execution done");
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
