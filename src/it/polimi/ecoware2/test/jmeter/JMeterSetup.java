package it.polimi.ecoware2.test.jmeter;

import it.polimi.ecoware2.test.utils.Commons;
import kg.apc.jmeter.threads.UltimateThreadGroup;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;


public class JMeterSetup
{
	
	private ResultCollector collector;
	private StandardJMeterEngine jmeter;
	public JMeterSetup() {
		
		jmeter = new StandardJMeterEngine();
		JMeterUtils.loadJMeterProperties("jmeter.properties");
		JMeterUtils.initLogging();
		JMeterUtils.initLocale();
	}
	
	public void setCollector(ResultCollector collector){
		this.collector = collector;
	}
	
	public void startTestWith(int stepDuration, Integer[] userSteps){
		
		HTTPSampler httpSampler = new HTTPSampler();
		httpSampler.setDomain(Commons.SERVER_HOST);
		httpSampler.setPort(Commons.SERVER_PORT);
		httpSampler.setPath(Commons.SERVER_PATH);
		httpSampler.setMethod("GET");

		LoopController loopController = new LoopController();
		loopController.setLoops(-1);
		loopController.addTestElement(httpSampler);
		loopController.setFirst(true);
		loopController.initialize();

		
		
		String threadsSchedule="";
		for(int i=0; i<userSteps.length; i++){
			threadsSchedule+="spawn("+userSteps[i]+","+(i*stepDuration)+"s,0s,"+stepDuration+"s,0s)";
		}

		System.out.println("Starting test with step duration: "+stepDuration+" user schedule: "+threadsSchedule);

		JMeterUtils.setProperty(UltimateThreadGroup.DATA_PROPERTY, threadsSchedule);
        UltimateThreadGroup threadGroup = new UltimateThreadGroup();
        threadGroup.setSamplerController(loopController);
        System.out.println(threadGroup.getData());
        
		HashTree testPlanTree = new HashTree();
		TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
		HashTree secondTree = new HashTree();
		HashTree thirdTree = new HashTree();
		thirdTree.add(httpSampler, new HashTree());
		thirdTree.add(collector);
		secondTree.add(threadGroup, thirdTree);
		testPlanTree.add(testPlan, secondTree);
		jmeter.configure(testPlanTree);
		
		try
		{
			jmeter.runTest();
		}
		catch (JMeterEngineException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
