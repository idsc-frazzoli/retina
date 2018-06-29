// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.tensor.Tensor;

// TODO move EventMap code here into static methods
public class SlamMapUtil {
  
  /** in first version, just update exact world frame cell with particle likelihoods
   * 
   * @param slamParticles particle set
   * @param occurrenceMap
   * @param gokartFramePos [m]
   */
  public static void updateOccurrenceMap(SlamParticle[] slamParticles, MapProvider occurrenceMap, double[] gokartFramePos) {
    // TODO only use the particles with the highest probability
    for (int i = 0; i < slamParticles.length; i++) {
      Tensor worldCoord = slamParticles[i].getGeometricLayer().toVector(gokartFramePos[0],gokartFramePos[1]);
      occurrenceMap.addValue(worldCoord, slamParticles[i].getParticleLikelihood());
    }
  }
  
  /**
   * 
   * @param currentExpectedPose
   * @param lastExpectedPose
   * @param normalizationMap
   */
  public static void updateNormalizationMap(Tensor currentExpectedPose, Tensor lastExpectedPose, MapProvider normalizationMap) {
    // ..
  }
}
