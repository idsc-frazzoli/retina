// code by mg
package ch.ethz.idsc.demo.mg.slam;

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
  private final double alpha;
  private final int numberOfParticles;

  SlamParticleSet(PipelineConfig pipelineConfig) {
    alpha = pipelineConfig.alpha.number().doubleValue();
    numberOfParticles = pipelineConfig.numberOfParticles.number().intValue();
    slamParticleSet = new SlamParticle[numberOfParticles];
    for (int i = 0; i < slamParticleSet.length; i++)
      slamParticleSet[i] = new SlamParticle();
  }

  // update the particle likelihooods
  // TODO move to SlamParticleUtil
  public void updateStateLikelihoods(double[] gokartFramePos, MapProvider likelihoodMap) {
    double sumOfLikelihoods = 0;
    for (int i = 0; i < numberOfParticles; i++) {
      // map go kart coordinates into world coordinates using the state estimate of the particle
      Tensor worldCoord = slamParticleSet[i].getGeometricLayer().toVector(gokartFramePos[0], gokartFramePos[1]);
      // get the likelihoodMap value of the computed world coordinate position and apply the actual update rule
      double updatedParticleLikelihood = slamParticleSet[i].getParticleLikelihood() + alpha * likelihoodMap.getValue(worldCoord);
      slamParticleSet[i].setParticleLikelihood(updatedParticleLikelihood);
      sumOfLikelihoods += updatedParticleLikelihood;
    }
    // normalize particle likelihoods
    for (int i = 0; i < numberOfParticles; i++)
      slamParticleSet[i].setParticleLikelihood(slamParticleSet[i].getParticleLikelihood() / sumOfLikelihoods);
  }

  // expected state is a weighted mean of all particles
  // TODO replace with better solution
  public Tensor getExpectedPose() {
    Tensor expectedPose = Tensors.of(Quantity.of(0, SI.METER), Quantity.of(0, SI.METER), DoubleScalar.of(0));
    for (int i = 0; i < numberOfParticles; i++) {
      Tensor pose = slamParticleSet[i].getPose();
      double likelihood = slamParticleSet[i].getParticleLikelihood();
      expectedPose = expectedPose.add(pose.multiply(RealScalar.of(likelihood)));
    }
    return expectedPose;
  }

  public int getNumberOfParticles() {
    return numberOfParticles;
  }

  public SlamParticle[] getParticles() {
    return slamParticleSet;
  }

  public SlamParticle getParticle(int index) {
    return slamParticleSet[index];
  }
}
