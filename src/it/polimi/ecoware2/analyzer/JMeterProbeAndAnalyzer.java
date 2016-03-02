package it.polimi.ecoware2.analyzer;

import it.polimi.ecoware2.executor.Allocation;
import it.polimi.ecoware2.test.utils.Bus;
import it.polimi.ecoware2.test.utils.Commons;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;


public class JMeterProbeAndAnalyzer extends ResultCollector
{

	private static final long serialVersionUID = 5423055497736341710L;
	
	private Runnable endCallback;
	
	private static CSVPrinter log = null;
	private long firstTs=-1;
	private long baseTs;
	private double currentAvgRt=0;
	private double current95Rt=0;
	private double currentAvgReq=0;
		
	private DescriptiveStatistics rt = new DescriptiveStatistics();
	private DescriptiveStatistics req = new DescriptiveStatistics();

	private ExecutorService executor=Executors.newFixedThreadPool(1);
	
	public JMeterProbeAndAnalyzer(String fileName, Runnable endCallback){
		
		super(null);
		
		this.endCallback = endCallback;
		
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
        FileWriter fileWriter;
		try
		{
			fileWriter = new FileWriter(fileName);
			log = new CSVPrinter(fileWriter, csvFileFormat);
			log.printRecord("timestamp (ms)", "elapsed time (ms)", "url", "rt", "avg", "p95", "mem (byte)", "cpu core", "users");
			log.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
      
		
	}
	public void sampleOccurred(SampleEvent event){
		
		Allocation a = (Allocation) Bus.getShared().get(Commons.CURRENT_ALLOCATION_KEY);
		executor.execute(()->{

			SampleResult result = event.getResult();
			long ts = System.currentTimeMillis();
			
			if(firstTs==-1){
				firstTs=ts;
				baseTs=ts;
			}
			
			rt.addValue(result.getLatency()/1E3);
			req.addValue(result.getAllThreads());
			
			if(ts>baseTs+Commons.SAMPLE_TIME*1000){
				currentAvgReq=req.getMean();
				currentAvgRt=rt.getMean();
				current95Rt=rt.getPercentile(95);
				baseTs=ts;
				rt.clear();
				req.clear();
				try
				{	
					AnalysisReport report = new AnalysisReport();
					report.setAvgResponseTime((float)currentAvgRt);
					report.setRequestNumber((int)currentAvgReq);
					
					Bus.getShared().put(Commons.ANALYSIS_KEY, report);

					String[] urlParts = result.getURL().toString().split("/");
					List<Object> values=Arrays.asList(ts, (ts-firstTs), urlParts[urlParts.length-1], result.getLatency()/1E3, currentAvgRt, current95Rt, a.getM()/1E9, a.getC(), currentAvgReq);
					List<String> record=values.stream().map(x -> x.toString()).collect(Collectors.toList());
					log.printRecord(record);
					log.flush();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			
		});
	}
	
	public void testEnded(){
		
		super.testEnded(null);
		
		System.out.println("END");

		endCallback.run();
		executor.shutdownNow();
		try
		{
			log.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
