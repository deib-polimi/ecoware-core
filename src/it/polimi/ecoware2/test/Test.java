package it.polimi.ecoware2.test;

import it.polimi.ecoware2.analyzer.JMeterProbeAndAnalyzer;
import it.polimi.ecoware2.test.jmeter.JMeterSetup;
import it.polimi.ecoware2.utils.Commons;


public abstract class Test
{
	private JMeterSetup jmeter; 
	private boolean end;
	
	protected void startJMeter(String busKey){
		String fileName = Commons.TEST_NAME+"-"+System.currentTimeMillis()+"--"+Commons.STEPS+"_"+Commons.STEP_DURATION+"_"+Commons.SAMPLE_TIME+"_"+Commons.CONTROL_PERIOD+".csv";
		jmeter=new JMeterSetup();
		jmeter.setCollector(new JMeterProbeAndAnalyzer(fileName, () -> { 
			System.out.println("end callback");
			end = true; 
		}, busKey));
		
		System.out.println("New JMeter test is going to be launched");
		System.out.println("Saving output on file "+fileName);
		
		jmeter.startTestWith(Commons.STEP_DURATION, Commons.STEPS, Commons.SERVER_HOST, Commons.SERVER_PORT, Commons.SERVER_PATH);

	}
	
	abstract public void start();
	
	abstract protected void refreshAllocation();
	
	// Refresh the bus with the current allocation fetched from AWS every SAMPLE_TIME seconds
	protected void startAllocationMonitoringLoop(){
		new Thread(() -> {
			int cont=0;
			while(!end){
				try
				{
					Thread.sleep(1000);
					cont+=1;
					if(cont % Commons.SAMPLE_TIME == 0){
						cont=0;
						refreshAllocation();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}).start();
	}
	
	protected void startPlanningAndExecutionMonitoringLoop(){
		new Thread(() -> {
			int cont=0;
			while(!end){
				try
				{
					Thread.sleep(1000);
					cont+=1;
					if(cont>(Commons.CONTROL_PERIOD)){
						cont=0;
						allocateAndExecute();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	abstract protected void allocateAndExecute();


}
