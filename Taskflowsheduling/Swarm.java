package PSO;

import utils.Constants;
import utils.GenerateMatrices;

import java.util.ArrayList;
import java.util.Iterator;


public class Swarm implements Iterable<SchedulerParticle>{
	
	public static double def_inc = 0.9;
	public static double def_inertia = 0.95;
	public static int def_partcnt = Constants.pop_size;
	public static double def_pinc = 0.9;

	double bestFitness;

	int bestParticleIndex;

	double bestPosition[];
	SchedulerFitnessFunction fitnessFunction;
	double globalIncrement;
	double inertia;
	double maxPosition[];
	double maxVelocity[];

	double minPosition[];

	double minVelocity[];
	int epochs;
	int part_cnt;
	double particleIncrement;
	SchedulerParticle particles[];
	SchedulerParticleUpdate SchedulerParticleUpdate;
	SchedulerParticle sampleParticle;
	ArrayList<SchedulerParticle> particlesList;
	
	
	public Swarm(int part_cnt, SchedulerParticle sampleParticle, SchedulerFitnessFunction fitnessFunction) {
		if (sampleParticle == null) throw new RuntimeException("Sample particle can't be null!");
		if (part_cnt <= 0) throw new RuntimeException("Number of particles should be greater than zero.");

		globalIncrement = def_inc;
		inertia = def_inertia;
		particleIncrement = def_pinc;
		epochs = 0;
		this.part_cnt = part_cnt;
		this.sampleParticle = sampleParticle;
		this.fitnessFunction = fitnessFunction;
		bestFitness = Double.NaN;
		bestParticleIndex = -1;

		SchedulerParticleUpdate = new SchedulerParticleUpdate(sampleParticle);
		particlesList = null;
	}

	public void evaluate() {
		if (particles == null) throw new RuntimeException("No particles in this swarm! May be you need to call Swarm.init() method");
		if (fitnessFunction == null) throw new RuntimeException("No fitness function in this swarm! May be you need to call Swarm.setSchedulerFitnessFunction() method");

		// Initialize
		if (Double.isNaN(bestFitness)) {
			bestFitness = Double.POSITIVE_INFINITY;
			bestParticleIndex = -1;
		}

		for (int i = 0; i < particles.length; i++) {
		
			double fit = fitnessFunction.evaluate(particles[i]);

			epochs++;

			if (fitnessFunction.isBetterThan(bestFitness, fit)) {
				bestFitness = fit; 
				bestParticleIndex = i;
				if (bestPosition == null) bestPosition = new double[sampleParticle.getDimension()];
				particles[bestParticleIndex].copyPosition(bestPosition);
			}

		}
	}

	public void startepoch() {

		if (particles == null) init();

		evaluate();
		update();

	}

	public double getBestFitness() {
		return bestFitness;
	}

	public SchedulerParticle getBestParticle() {
		return particles[bestParticleIndex];
	}

	public int getBestParticleIndex() {
		return bestParticleIndex;
	}

	public double[] getBestPosition() {
		return bestPosition;
	}

	public SchedulerFitnessFunction getSchedulerFitnessFunction() {
		return fitnessFunction;
	}

	public double getGlobalIncrement() {
		return globalIncrement;
	}

	public double getInertia() {
		return inertia;
	}

	public double[] getMaxPosition() {
		return maxPosition;
	}

	public double[] getMaxVelocity() {
		return maxVelocity;
	}

	public double[] getMinPosition() {
		return minPosition;
	}

	public double[] getMinVelocity() {
		return minVelocity;
	}

	public int getNumberOfEvaluations() {
		return epochs;
	}

	public int getNumberOfParticles() {
		return part_cnt;
	}

	public SchedulerParticle getParticle(int i) {
		return particles[i];
	}

	public double getParticleIncrement() {
		return particleIncrement;
	}

	public SchedulerParticle[] getParticles() {
		return particles;
	}

	public SchedulerParticleUpdate getSchedulerParticleUpdate() {
		return SchedulerParticleUpdate;
	}

	public SchedulerParticle getSampleParticle() {
		return sampleParticle;
	}
	
	public void init() {

		particles = new SchedulerParticle[part_cnt];

		if (maxPosition == null) throw new RuntimeException("maxPosition array is null!");
		if (minPosition == null) throw new RuntimeException("maxPosition array is null!");
		if (maxVelocity == null) {

			int dim = sampleParticle.getDimension();
			maxVelocity = new double[dim];
			for (int i = 0; i < dim; i++)
				maxVelocity[i] = (maxPosition[i] - minPosition[i]) / 2.0;
		}
		if (minVelocity == null) {
			
			int dim = sampleParticle.getDimension();
			minVelocity = new double[dim];
			for (int i = 0; i < dim; i++)
				minVelocity[i] = -maxVelocity[i];
		}

		for (int i = 0; i < part_cnt; i++) {
			particles[i] = (SchedulerParticle) new SchedulerParticle();
			particles[i].init(maxPosition, minPosition, maxVelocity, minVelocity);
		}

	}

	public Iterator<SchedulerParticle> iterator() {
		if (particlesList == null) {
			particlesList = new ArrayList<SchedulerParticle>(particles.length);
			for (int i = 0; i < particles.length; i++)
				particlesList.add(particles[i]);
		}

		return particlesList.iterator();
	}

	public void setBestParticleIndex(int bestParticle) {
		bestParticleIndex = bestParticle;
	}

	public void setBestPosition(double[] bestPosition) {
		this.bestPosition = bestPosition;
	}

	public void setSchedulerFitnessFunction(SchedulerFitnessFunction fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
	}

	public void setGlobalIncrement(double globalIncrement) {
		this.globalIncrement = globalIncrement;
	}

	public void setInertia(double inertia) {
		this.inertia = inertia;
	}
	
	public void setMaxMinVelocity(double maxVelocity) {
		if (sampleParticle == null) throw new RuntimeException("Need to set sample particle before calling this method (use Swarm.setSampleParticle() method)");
		int dim = sampleParticle.getDimension();
		this.maxVelocity = new double[dim];
		minVelocity = new double[dim];
		for (int i = 0; i < dim; i++) {
			this.maxVelocity[i] = maxVelocity;
			minVelocity[i] = -maxVelocity;
		}
	}

	public void setMaxPosition(double maxPosition) {
		if (sampleParticle == null) throw new RuntimeException("Need to set sample particle before calling this method (use Swarm.setSampleParticle() method)");
		int dim = sampleParticle.getDimension();
		this.maxPosition = new double[dim];
		for (int i = 0; i < dim; i++)
			this.maxPosition[i] = maxPosition;
	}

	public void setMaxPosition(double[] maxPosition) {
		this.maxPosition = maxPosition;
	}

	public void setMaxVelocity(double[] maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	public void setMinPosition(double minPosition) {
		if (sampleParticle == null) throw new RuntimeException("Need to set sample particle before calling this method (use Swarm.setSampleParticle() method)");
		int dim = sampleParticle.getDimension();
		this.minPosition = new double[dim];
		for (int i = 0; i < dim; i++)
			this.minPosition[i] = minPosition;
	}

	public void setMinPosition(double[] minPosition) {
		this.minPosition = minPosition;
	}

	public void setMinVelocity(double minVelocity[]) {
		this.minVelocity = minVelocity;
	}

	public void setNumberOfEvaluations(int epochs) {
		this.epochs = epochs;
	}

	public void setNumberOfParticles(int part_cnt) {
		this.part_cnt = part_cnt;
	}

	public void setParticleIncrement(double particleIncrement) {
		this.particleIncrement = particleIncrement;
	}

	public void setParticles(SchedulerParticle[] particle) {
		particles = particle;
		particlesList = null;
	}

	public void setSchedulerParticleUpdate(SchedulerParticleUpdate SchedulerParticleUpdate) {
		this.SchedulerParticleUpdate = SchedulerParticleUpdate;
	}

	public void setSampleParticle(SchedulerParticle sampleParticle) {
		this.sampleParticle = sampleParticle;
	}
	public int size() {
		return particles.length;
	}

	@Override
	public String toString() {
		String str = "";

		if (particles != null) str += "Swarm size: " + particles.length + "\n";

		if ((minPosition != null) && (maxPosition != null)) {
			str += "Position ranges:\t";
			for (int i = 0; i < maxPosition.length; i++)
				str += "[" + minPosition[i] + ", " + maxPosition[i] + "]\t";
		}

		if ((minVelocity != null) && (maxVelocity != null)) {
			str += "\nVelocity ranges:\t";
			for (int i = 0; i < maxVelocity.length; i++)
				str += "[" + minVelocity[i] + ", " + maxVelocity[i] + "]\t";
		}

		if (sampleParticle != null) str += "\nSample particle: " + sampleParticle;

		if (particles != null) {
			str += "\nParticles:";
			for (int i = 0; i < particles.length; i++) {
				str += "\n\tParticle: " + i + "\t";
				str += particles[i].toString();
			}
		}
		str += "\n";

		return str;
	}
	public String toStringStats() {
		String stats = "";
		if (!Double.isNaN(bestFitness)) {
			stats += "Best fitness: " + bestFitness + "\nBest position: \t[";
			for (int i = 0; i < bestPosition.length; i++)
				stats += bestPosition[i] + (i < (bestPosition.length - 1) ? ", " : "");
			stats += "]\nNumber of evaluations: " + epochs + "\n";
		}
		return stats;
	}

	public void update() {

		for (int i = 0; i < particles.length; i++) {
			
			SchedulerParticleUpdate.update(this, particles[i]);
			particles[i].applyConstraints(minPosition, maxPosition, minVelocity, maxVelocity);
		}
	}
}
