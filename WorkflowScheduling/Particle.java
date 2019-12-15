package org.workflowsim.scheduling;

import org.cloudbus.cloudsim.Log;

public class Particle {

	double best_fit;
	double p_best[];
	double fitness;
	double position[];
	double velocity[];
	int tasks,Vm;
	
	public Particle(int task,int vm) {
		
		position = new double[task];
		p_best = new double[task];
		velocity = new double[task];
		best_fit = Double.NaN;
		fitness = Double.NaN;
		tasks=task;
		Vm=vm;
		for (int i = 0; i < position.length; i++)
			p_best[i] = Double.NaN;	
		
		for (int i = 0; i < task; i++) {
        	
            position[i] = (int)Vm * Math.random();
            velocity[i] = Math.random();
          
        }
	}
	
	public void eval() {
		
		double fit=fitnessfunc(position);
		//System.out.println(fit);
		
		if(Double.isNaN(fitness) || fit<fitness) {
			
			fitness=fit;
			p_best=position;
		}
	}

	private double fitnessfunc(double[] position2) {
		
		double totalcost=0;
		double mat[][]=Matrix.getcost();
		for(int i=0;i<tasks;i++)	totalcost+=(mat[i][(int) position[i]]);
		return totalcost;
	}
	
	public void update(double g_best[]) {
		
		double C=1.0,W=0.5;
		for (int i = 0; i < tasks; ++i) {
	            velocity[i] = W * velocity[i] + C * Math.random() * (p_best[i] - position[i]) + C * Math.random() * (g_best[i] - position[i]);
	            position[i] = (int)(position[i] + velocity[i]);
	            
	            if(position[i]>=Vm)	position[i]=Vm-1;
	            if(position[i]<0)	position[i]=0;
	    }		 
	}
	
	
}
