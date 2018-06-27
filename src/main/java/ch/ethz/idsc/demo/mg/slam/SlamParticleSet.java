// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.Random;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// provides a set of SlamParticles
public class SlamParticleSet {
  private final SlamParticle[] slamParticleSet;
  private final Random generator;
  private final double alpha;
  private final int numberOfParticles;

  SlamParticleSet(PipelineConfig pipelineConfig) {
    generator = new Random();
    alpha = pipelineConfig.alpha.number().doubleValue();
    numberOfParticles = pipelineConfig.numberOfParticles.number().intValue();
    slamParticleSet = new SlamParticle[numberOfParticles];
    double initParticleLikelihood = 1.0 / numberOfParticles;
    for (int i = 0; i < slamParticleSet.length; i++) {
      slamParticleSet[i] = new SlamParticle(initParticleLikelihood);
    }
  }

  // propagate each single particle using wheel odometry
  public void propagateStateEstimate(double linVelAvg, double linVelStd, double angVelStd, double dT) {
    double vx = getGaussian(linVelAvg, linVelStd);
    double vangle = getGaussian(0, angVelStd);
    // Tensor deltaPose = Tensors.of(Quantity.of(x, SI.VELOCITY), Quantity.of(0, SI.VELOCITY), DoubleScalar.of(angle));
    Tensor deltaPose = Tensors.vector(vx, 0, vangle).multiply(RealScalar.of(dT));
    for (int i = 0; i < numberOfParticles; i++)
      slamParticleSet[i].propagateStateEstimate(deltaPose);
  }

  // update the particle likelihooods
  public void updateStateLikelihoods(double[] gokartFramePos, MapProvider likelihoodMap) {
    double sumOfLikelihoods = 0;
    double maxValue = likelihoodMap.getMaxValue();
    // nice hack
    if(maxValue == 0)
      maxValue = 1;
    for (int i = 0; i < numberOfParticles; i++) {
      // map go kart coordinates into world coordinates using the state estimate of the particle
      Tensor worldCoord = slamParticleSet[i].getGeometricLayer().toVector(gokartFramePos[0], gokartFramePos[1]);
      // get the likelihoodMap value of the computed world coordinate position and apply the actual update rule
      double updatedParticleLikelihood = slamParticleSet[i].getParticleLikelihood() + alpha * likelihoodMap.getValue(worldCoord) / maxValue;
      slamParticleSet[i].setParticleLikelihood(updatedParticleLikelihood);
      sumOfLikelihoods += updatedParticleLikelihood;
    }
    // normalize particle likelihoods
    for (int i = 0; i < numberOfParticles; i++)
      slamParticleSet[i].setParticleLikelihood(slamParticleSet[i].getParticleLikelihood() / sumOfLikelihoods);
  }

  public SlamParticle[] getParticles() {
    return slamParticleSet;
  }

  // expected state is a weighted mean of all particles
  // TODO is this correct?
  public Tensor getExpectedPose() {
    Tensor expectedPose = Tensors.of(Quantity.of(0, SI.METER), Quantity.of(0, SI.METER), DoubleScalar.of(0));
    for (int i = 0; i < numberOfParticles; i++) {
      Tensor pose = slamParticleSet[i].getPose();
      double likelihood = slamParticleSet[i].getParticleLikelihood();
      expectedPose = expectedPose.add(pose.multiply(RealScalar.of(likelihood)));
    }
    return expectedPose;
  }

  // distributes particles uniformly around given initial pose
  public void setInitialDistribution(Tensor pose) {
    // TODO use deterministic distribution depending on particleNumber, probably move to RandomUtil?
    for (int i = 0; i < numberOfParticles; i++)
      slamParticleSet[i].setPose(pose);
  }

  // for testing
  public void setPose(Tensor pose) {
    for (int i = 0; i < numberOfParticles; i++)
      slamParticleSet[i].setPose(pose);
  }

  // TODO probably move all methods dealing with random numbers etc to RandomUtil?
  private double getGaussian(double mean, double standardDeviation) {
    return generator.nextGaussian() * standardDeviation + mean;
  }

  public int getNumberOfParticles() {
    return numberOfParticles;
  }

  public SlamParticle getParticle(int index) {
    return slamParticleSet[index];
  }
}
