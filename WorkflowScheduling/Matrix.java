package org.workflowsim.scheduling;

public class Matrix {

	public static double cost[][];
	
	public Matrix(){
		
		cost = new double[1000][1000];
		for(int i=0;i<1000;i++)	for(int j=0;j<1000;j++)	cost[i][j]=1000+10*Math.random();		
		
	}
	
	public static double[][] getcost(){
		
		return cost;
	}

}
