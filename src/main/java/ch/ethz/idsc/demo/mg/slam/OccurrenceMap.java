// code by mg
package ch.ethz.idsc.demo.mg.slam;

// provides the occurrence map
public class OccurrenceMap {
  OccurrenceMap() {
    // initialize array to zero
  }

  // update requires SlamParticleSet and a gaussian distribution.
  // TODO precompute gaussian distributions with different variances?
  public void update(double[] gokartCoordPos, SlamParticle[] slamParticles) {
    // we generate a Gaussian distribution for each particle
    // the mean of the Gaussian is is world coordinate position of gokartCoorPos
    // -> need to transform from gokart frame to world frame with provided particle pose
    // --
    // the variance of the Gaussian depends on the distance from sensor to projected event pos
    // TODO the distance between sensor and each pixel projected into world coord can be precomputed and stored in a lookup table
    double distanceToEvent = Math.sqrt(gokartCoordPos[0] * gokartCoordPos[0] + gokartCoordPos[1] * gokartCoordPos[1]);
    // each Gaussian distribution is weighted with the particle likelihood and then added to the occurrenceMap
  }
}
