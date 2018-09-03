// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.util.calibration.GokartToImageInterface;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/** methods for occurrence map update in SLAM algorithm */
/* package */ enum SlamOccurrenceMapStepUtil {
  ;
  /** update occurrence map with particles. in v1.0, counting events in each cell
   * 
   * @param slamParticles particle set
   * @param occurrenceMap
   * @param eventGokartFrame [m] position of event in go kart frame
   * @param relevantParticles number of particles with highest likelihoods used for update */
  public static void updateOccurrenceMap(SlamParticle[] slamParticles, MapProvider occurrenceMap, double[] eventGokartFrame, int relevantParticles) {
    double adaptiveWeightFactor = adaptiveEventWeightening(eventGokartFrame);
    // sort in descending order of likelihood
    Stream.of(slamParticles) //
        .parallel() //
        .sorted(SlamParticleLikelihoodComparator.INSTANCE) //
        .limit(relevantParticles) //
        .collect(Collectors.toList());
    for (int i = 0; i < relevantParticles; i++) {
      Tensor worldCoord = new Se2Bijection(slamParticles[i].getPoseUnitless()).forward() //
          .apply(Tensors.vectorDouble(eventGokartFrame));
      occurrenceMap.addValue(worldCoord, adaptiveWeightFactor * slamParticles[i].getParticleLikelihood());
    }
  }

  /** adapts the event weight based on e.g. distance to sensor
   * 
   * @param gokartFramePos [m] position of event in go kart frame
   * @return adaptiveWeightFactor [-] */
  // TODO implement and test
  private static double adaptiveEventWeightening(double[] gokartFramePos) {
    return 1;
  }

  /** updates the normalization map
   * 
   * @param currentExpectedPose
   * @param lastExpectedPose
   * @param normalizationMap */
  // TODO unused due to computational complexity
  public static void updateNormalizationMap(Tensor currentExpectedPose, Tensor lastExpectedPose, MapProvider normalizationMap,
      ImageToGokartInterface imageToGokartLookup, GokartToImageInterface gokartToImageUtil, int width, int height, double lookAheadDistance) {
    // use of hash set since we want a list of unique cells
    Set<Integer> seenCells = new HashSet<>();
    // find all cells in the map which were seen on the sensor with current or last expected pose
    GeometricLayer layerCurrent = GeometricLayer.of(GokartPoseHelper.toSE2Matrix(lastExpectedPose));
    GeometricLayer layerLast = GeometricLayer.of(GokartPoseHelper.toSE2Matrix(currentExpectedPose));
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        double[] gokartCoord = imageToGokartLookup.imageToGokart(i, j);
        if (gokartCoord[0] < lookAheadDistance) {
          // get cell index number for event position using last expected pose
          Tensor worldCoordLast = layerLast.toVector(gokartCoord[0], gokartCoord[1]);
          int cellIndexLast = normalizationMap.getCellIndex(worldCoordLast.Get(0).number().doubleValue(), worldCoordLast.Get(1).number().doubleValue());
          if (cellIndexLast != normalizationMap.getNumberOfCells())
            seenCells.add(cellIndexLast);
          // get cell index number for event position using current expected pose
          Tensor worldCoordCurrent = layerCurrent.toVector(gokartCoord[0], gokartCoord[1]);
          int cellIndexCurrent = normalizationMap.getCellIndex( //
              worldCoordCurrent.Get(0).number().doubleValue(), //
              worldCoordCurrent.Get(1).number().doubleValue());
          if (cellIndexCurrent != normalizationMap.getNumberOfCells())
            seenCells.add(cellIndexCurrent);
        }
      }
    }
    // for the center of all these cells, compute image plane location for last and current expected pose and compute norm
    synchronized (seenCells) {
      GeometricLayer layerCurrentInverse = GeometricLayer.of(Inverse.of(GokartPoseHelper.toSE2Matrix(currentExpectedPose)));
      GeometricLayer layerLastInverse = GeometricLayer.of(Inverse.of(GokartPoseHelper.toSE2Matrix(lastExpectedPose)));
      for (Integer cell : seenCells) {
        // find world coordinates of cell middle point
        double[] cellWorldCoord = normalizationMap.getCellCoord(cell);
        // transform to go kart frame for current and last go kart pose
        Tensor gokartCoordCurrent = layerCurrentInverse.toVector(cellWorldCoord[0], cellWorldCoord[1]);
        Tensor gokartCoordLast = layerLastInverse.toVector(cellWorldCoord[0], cellWorldCoord[1]);
        // transform to image plane
        Tensor imageCoordCurrent = gokartToImageUtil.gokartToImage(gokartCoordCurrent);
        Tensor imageCoordLast = gokartToImageUtil.gokartToImage(gokartCoordLast);
        // compute norm and add value
        normalizationMap.addValue(cell, Norm2Squared.between(imageCoordCurrent, imageCoordLast).number().doubleValue());
      }
    }
  }
}
