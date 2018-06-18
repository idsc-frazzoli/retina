// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.tensor.Tensor;

// provides a set of SlamParticles
public class SlamParticleSet {
  private final SlamParticle[] slamParticleSet;
  private final double alpha;
  private final int numberOfParticles;

  SlamParticleSet(PipelineConfig pipelineConfig) {
    alpha = pipelineConfig.alpha.number().doubleValue();
    numberOfParticles = pipelineConfig.numberOfParticles.number().intValue();
    slamParticleSet = new SlamParticle[numberOfParticles];
    for (int i = 0; i < numberOfParticles; i++) {
      slamParticleSet[i] = new SlamParticle();
    }
  }

  // propagate each single particle using wheel odometry
  public void propagateStateEstimate() {
    for (int i = 0; i < numberOfParticles; i++)
      slamParticleSet[i].propagateStateEstimate();
  }

  // update the particle likelihooods
  public void updateStateLikelihoods(double[] gokartFramePos, MapProvider likelihoodMap) {
    for (int i = 0; i < numberOfParticles; i++) {
      // map go kart coordinates into world coordinates using the state estimate of the particle
      Tensor worldCoord = slamParticleSet[i].getWorldCoord(gokartFramePos);
      // get the likelihoodMap value of the computed world coordinate position and apply the actual update rule
      double updatedParticleLikelihood = slamParticleSet[i].getParticleLikelihood() + alpha * likelihoodMap.getValue(worldCoord);
      slamParticleSet[i].setParticleLikelihood(updatedParticleLikelihood);
    }
  }

  // gets an array with the poses of all the particles
  public SlamParticle[] getParticles() {
    return slamParticleSet;
  }

  // expected state is a weighted mean of all particles
  public Tensor getExpectedPose() {
    Tensor expectedPose = null;
    for (int i = 0; i < numberOfParticles; i++) {
      Tensor pose = slamParticleSet[i].getPose();
      double likelihood = slamParticleSet[i].getParticleLikelihood();
    }
    return expectedPose;
  }

  // for testing
  public void setPose(Tensor pose) {
    for (int i = 0; i < numberOfParticles; i++)
      slamParticleSet[i].setPose(pose);
  }
}
