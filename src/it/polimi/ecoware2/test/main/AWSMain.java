package it.polimi.ecoware2.test.main;

import it.polimi.ecoware2.analyzer.AWSProbe;
import it.polimi.ecoware2.analyzer.JMeterProbeAndAnalyzer;
import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.test.jmeter.JMeterSetup;
import it.polimi.ecoware2.test.utils.Bus;
import it.polimi.ecoware2.test.utils.Commons;

import com.amazonaws.regions.Regions;


public class AWSMain
{
	
	private static JMeterSetup jmeter;

	private static Integer[] userPerStep = {50, 10};
	private static int step = 3;
	private static int numStep;
	private static int resChange = 10;
	private static boolean end = false;
	private static AWSProbe probe;
	
	
	public static void main(String[] args) throws Exception {
		
		String awsAccessKey = args[0];
		String awsSecretKey = args[1];
		String name = args[2];
		
		if(args!=null && args.length>3){
			step=Integer.parseInt(args[3]);
			resChange=Integer.parseInt(args[4]);
			
			Integer[] steps=new Integer[args.length-5];
			for(int i=5; i<args.length; i++){
					steps[i-5]=Integer.parseInt(args[i]);
			}
			
			userPerStep = steps;
		}
		
		
		String fileName = name+"-"+System.currentTimeMillis()+"--"+arrayToString(userPerStep)+"_"+step+"_"+numStep+"_"+resChange+".csv";
	
		System.out.println("New JMeter test is going to be launched");
		System.out.println("Resource will change every "+resChange+" seconds");
		System.out.println("Saving output on file "+fileName);

		numStep = userPerStep.length;
		
		jmeter=new JMeterSetup();
		jmeter.setCollector(new JMeterProbeAndAnalyzer(fileName, () -> { 
			System.out.println("end callback");
			end = true; }));
		
		probe = new AWSProbe(new Allocation(2*(long)1E9, 1), awsAccessKey, awsSecretKey, Regions.US_WEST_2, Commons.AWS_SCALE_GROUP);
		Bus.getShared().put(Commons.CURRENT_ALLOCATION_KEY, probe.getCurrentAllocation());
	
		jmeter.startTestWith(step, userPerStep);

		new Thread(() -> {
			int cont=0;
				while(!end){
					try
					{
						Thread.sleep(1000);
						cont+=1;
						if(cont % 30 == 0){
							cont=0;
							probe.refreshCurrentAllocation();
							Bus.getShared().put(Commons.CURRENT_ALLOCATION_KEY, probe.getCurrentAllocation());
							System.out.println(probe.getCurrentAllocation());
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
