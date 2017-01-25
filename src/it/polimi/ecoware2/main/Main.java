package it.polimi.ecoware2.main;

import it.polimi.ecoware2.test.AWSAutoscalingTest;
import it.polimi.ecoware2.test.Test;

import java.io.File;


public class Main
{

	public static void main(String[] args)
	{
		Test test = new AWSAutoscalingTest(new File("example_test.properties"));
		test.start();
	}

}
