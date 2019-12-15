package org.workflowsim.scheduling;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowSimTags;

public class PSOScheduler extends BaseSchedulingAlgorithm {

	@Override
	public void run() throws Exception {
		
		List vmList = getVmList();
		int csize = getCloudletList().size();
		int vsize = vmList.size();
		
		int allmat[]=PSO(csize,vsize);
	
		
		for (int j = 0; j < csize; j++) {
            Cloudlet cloudlet = (Cloudlet) getCloudletList().get(j);
            CondorVM vm = (CondorVM) vmList.get(j%vsize+1);
            if (vm.getState() == WorkflowSimTags.VM_STATUS_IDLE) {
              
                cloudlet.setVmId(vm.getId());
                getScheduledList().add(cloudlet);
                ((CondorVM) vm).setState(WorkflowSimTags.VM_STATUS_BUSY);
            }
            
        }		
	}

	private int[] PSO(int csize,int vsize) {
		
		double bestfit=Double.NaN;
		double g_best[]=new double[csize];
		new Matrix();
		
		Particle[] swarm = new Particle[15];
		
		for(int i=0;i<15;i++)	swarm[i]= new Particle(csize,vsize);
		
		for(int i=0;i<100;i++) {
			
			for(int j=0;j<15;j++) {
				
				swarm[j].eval();
				
				if( Double.isNaN(bestfit) || bestfit<swarm[j].fitness ) {
					
					bestfit=swarm[j].fitness;
					g_best=swarm[j].position;					
				}
			}
			for(int j=0;j<15;j++) swarm[j].update(g_best);
		}
		
		int ans[]=new int[csize];
		
		for(int i=0;i<csize;i++)	ans[i]=(int)g_best[i];
		
		return ans;
	}

}
