package PSO;

import utils.Constants;
import utils.GenerateMatrices;

public class SchedulerParticleUpdate{
    private static double W = 0.9;
    private static double C = 2.0;

    SchedulerParticleUpdate(SchedulerParticle particle) {
    }

    public void update(Swarm swarm, SchedulerParticle particle) {
        double[] v = particle.getVelocity();
        double[] x = particle.getPosition();
        double[] pbest = particle.getBestPosition();
        double[] gbest = swarm.getBestPosition();

        for (int i = 0; i < Constants.task_count; ++i) {
            v[i] = W * v[i] + C * Math.random() * (pbest[i] - x[i]) + C * Math.random() * (gbest[i] - x[i]);
            x[i] = (int)(x[i] + v[i]);
        }
    }
}