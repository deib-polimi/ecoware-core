package it.polimi.ecoware2.main;

import it.polimi.ecoware2.test.AWSAutoscalingTest;
import it.polimi.ecoware2.test.Test;


public class Main
{

	public static void main(String[] args)
	{
		Test test = new AWSAutoscalingTest();
		test.start();
	}

}
