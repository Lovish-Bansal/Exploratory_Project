package PSO;

import utils.Constants;
import utils.GenerateMatrices;

public class SchedulerFitnessFunction{
    private static double[][] execMatrix, commMatrix;
    
    SchedulerFitnessFunction() {
 
        commMatrix = GenerateMatrices.getCommMatrix();
        execMatrix = GenerateMatrices.getExecMatrix();
    }
    
	public boolean isBetterThan(double fitness, double otherValue) {

		if (otherValue < fitness) return true;
		return false;
	}
    
    public double evaluate(SchedulerParticle particle) {
    	
		double position[] = particle.getPosition();
		double alpha = 0.1;
		double fit = alpha * TotalTime(position) + (1 - alpha) * Makespan(position);
		particle.setFitness(fit);
		return fit;
	}

    private double TotalTime(double[] position) {
        double totalCost = 0;
        for (int i = 0; i < Constants.task_count; i++) {
            int dcId = (int) position[i];
            totalCost += execMatrix[i][dcId] + commMatrix[i][dcId];
        }
        return totalCost;
    }

    public double Makespan(double[] position) {
        double makespan = 0;
        double[] work = new double[Constants.data_count];

        for (int i = 0; i < Constants.task_count; i++) {
            int dcId = (int) position[i];
            work[dcId] += execMatrix[i][dcId] + commMatrix[i][dcId];
            makespan = Math.max(makespan, work[dcId]);
        }
        return makespan;
    }
}
