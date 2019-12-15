package PSO;

import utils.Constants;
import utils.GenerateMatrices;

public class PSO {
    private static Swarm swarm;
    private static SchedulerParticle particles[];
    private static SchedulerFitnessFunction ff = new SchedulerFitnessFunction();

    public PSO() {
        initParticles();
    }


    public double[] run() {
        swarm = new Swarm(Constants.pop_size, new SchedulerParticle(), ff);

        swarm.setMinPosition(0);
        swarm.setMaxPosition(Constants.data_count - 1);
        swarm.setMaxMinVelocity(0.5);
        swarm.setParticles(particles);
        swarm.setSchedulerParticleUpdate(new SchedulerParticleUpdate(new SchedulerParticle()));

        for (int i = 0; i < Constants.epochs; i++) {
        	
            swarm.startepoch();
            
            if (i % 10 == 0) {
                System.out.println("Global best at iteration " + i + " : " + swarm.getBestFitness());
            }
        }

        printBestFitness();

        System.out.println("The best solution is: ");
        SchedulerParticle bestParticle = (SchedulerParticle) swarm.getBestParticle();
        System.out.println(bestParticle.toString());

        return swarm.getBestPosition();
    }

    private static void initParticles() {
        particles = new SchedulerParticle[Constants.pop_size];
        for (int i = 0; i < Constants.pop_size; ++i)
            particles[i] = new SchedulerParticle();
    }

    public void printBestFitness() {
        System.out.println("\nBest fitness value: " + swarm.getBestFitness() +
                "\nBest makespan: " + ff.Makespan(swarm.getBestParticle().getBestPosition()));
    }
}
