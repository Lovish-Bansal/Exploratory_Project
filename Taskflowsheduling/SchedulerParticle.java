package PSO;

import utils.Constants;
import utils.GenerateMatrices;

public class SchedulerParticle{
	
	double best_fit;
	double p_best[];
	double fitness;
	double position[];
	double velocity[];
	
    SchedulerParticle() {
    	
    	allocate(Constants.task_count);
        
        for (int i = 0; i < Constants.task_count; i++) {
        	
            position[i] = (int)(Constants.data_count*Math.random());
            velocity[i] = Math.random();
          
        }
    }
    
    public void allocate(int dimension) {
		position = new double[dimension];
		p_best = new double[dimension];
		velocity = new double[dimension];
		best_fit = Double.NaN;
		fitness = Double.NaN;
		for (int i = 0; i < position.length; i++)
			p_best[i] = Double.NaN;
	}
    
    public void applyConstraints(double[] minPosition, double[] maxPosition, double[] minVelocity, double[] maxVelocity) {

		for (int i = 0; i < position.length; i++) {
			
			position[i] = (minPosition[i] > position[i] ? minPosition[i] : position[i]);
			position[i] = (maxPosition[i] < position[i] ? maxPosition[i] : position[i]);
			velocity[i] = (minVelocity[i] > velocity[i] ? minVelocity[i] : velocity[i]);
			velocity[i] = (maxVelocity[i] < velocity[i] ? maxVelocity[i] : velocity[i]);
		}
	}
    
    public void copyPosition(double positionCopy[]) {
		for (int i = 0; i < position.length; i++)
			positionCopy[i] = position[i];
	}

	public void copyPosition2Best() {
		for (int i = 0; i < position.length; i++)
			p_best[i] = position[i];
	}

	public double getBestFitness() {
		return best_fit;
	}

	public double[] getBestPosition() {
		return p_best;
	}

	public int getDimension() {
		return position.length;
	}

	public double getFitness() {
		return fitness;
	}

	public double[] getPosition() {
		return position;
	}

	public double[] getVelocity() {
		return velocity;
	}
	
	public void init(double maxPosition[], double minPosition[], double maxVelocity[], double minVelocity[]) {
		for (int i = 0; i < position.length; i++) {
			
			position[i] = (maxPosition[i] - minPosition[i]) * Math.random() + minPosition[i];
			velocity[i] = (maxVelocity[i] - minVelocity[i]) * Math.random() + minVelocity[i];

			p_best[i] = Double.NaN;
		}
	}
	
	public void setBestFitness(double best_fit) {
		this.best_fit = best_fit;
	}

	public void setBestPosition(double[] p_best) {
		this.p_best = p_best;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
		boolean maximize=false;
		if ((maximize && (fitness > best_fit))
				|| (!maximize && (fitness < best_fit))
				|| Double.isNaN(best_fit)) {
			copyPosition2Best();
			best_fit = fitness;
		}
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	public void setVelocity(double[] velocity) {
		this.velocity = velocity;
	}

    public String toString() {
        String output = "";
        for (int i = 0; i < Constants.data_count; i++) {
            String tasks = "";
            int task_count = 0;
            for (int j = 0; j < Constants.task_count; j++) {
                if (i == (int) getPosition()[j]) {
                    tasks += (tasks.isEmpty() ? "" : " ") + j;
                    ++task_count;
                }
            }
            if (tasks.isEmpty()) output += "There is no tasks associated to Data Center " + i + "\n";
            else
                output += "There are " + task_count + " tasks associated to Data Center " + i + " and they are " + tasks + "\n";
        }
        return output;
    }
}
