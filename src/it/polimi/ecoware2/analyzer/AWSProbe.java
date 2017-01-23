package it.polimi.ecoware2.analyzer;

import it.polimi.ecoware2.executor.Allocation;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;


public class AWSProbe
{
	private final Allocation vmFlavor;
	private Allocation currentAllocation;
	
	private final String autoscaleGroupName;
	
	private AmazonAutoScalingClient ec2;
	
	public AWSProbe(Allocation vmFlavor, String awsKey, String awsSecret, Regions region, String autoscaleGroupName){
		
		this.vmFlavor = vmFlavor;
		this.autoscaleGroupName = autoscaleGroupName;
		
		ec2 = new AmazonAutoScalingClient(new BasicAWSCredentials(awsKey, awsSecret));
		ec2.setRegion(Region.getRegion(region));
		fetchAllocationFromAWS();
		
	}
	
	public synchronized void fetchAllocationFromAWS(){
		DescribeAutoScalingGroupsResult result = ec2.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest().withAutoScalingGroupNames(autoscaleGroupName));
		long size = result.getAutoScalingGroups().get(0).getInstances().stream().filter((x) -> x.getLifecycleState().equals("InService")).count();
		this.currentAllocation = new Allocation(vmFlavor.getM() * size, ((float)(vmFlavor.getC() * (int) size)));
	}
	
	public synchronized Allocation getCurrentAllocation() {
		return this.currentAllocation;
	}
	
	
	
}
