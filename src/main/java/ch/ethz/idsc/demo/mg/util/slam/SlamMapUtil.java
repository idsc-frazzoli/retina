// code by mg
package ch.ethz.idsc.demo.mg.util.slam;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.util.calibration.GokartToImageInterface;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/** utilities to manipulate maps */
public enum SlamMapUtil {
  ;
  /** update occurrence map with particles. in v1.0, counting events in each cell
   * 
   * @param slamParticles particle set
   * @param occurrenceMap
   * @param gokartFramePos [m] position of event in go kart frame
   * @param particleRange number of particles with highest likelihoods used for update */
  public static void updateOccurrenceMap(SlamParticle[] slamParticles, MapProvider occurrenceMap, double[] gokartFramePos, int particleRange) {
    // sort in descending order of likelihood
    double adaptiveWeightFactor = adaptiveEventWeightening(gokartFramePos);
    adaptiveWeightFactor = 1;
    Arrays.sort(slamParticles, 0, particleRange, SlamParticleUtil.SlamCompare);
    for (int i = 0; i < particleRange; i++) {
      Tensor worldCoord = slamParticles[i].getGeometricLayer().toVector(gokartFramePos[0], gokartFramePos[1]);
      occurrenceMap.addValue(worldCoord, adaptiveWeightFactor * slamParticles[i].getParticleLikelihood());
    }
  }

  /** adapts the event weight based on e.g. distance to sensor
   * 
   * @param gokartFramePos [m] positoin of event in go kart frame
   * @return adaptiveWeightFactor */
  // TODO in future, the weight could be adapted by where we expect the next waypoint to appear
  private static double adaptiveEventWeightening(double[] gokartFramePos) {
    // compute distance metric
    double distance = Math.sqrt(gokartFramePos[0] * gokartFramePos[0]);
    return 1 + distance;
  }

  /** update occurrence map with lidar ground truth
   * 
   * @param gokartLidarPose unitless representation ground truth pose provided by lidar
   * @param occurrenceMap
   * @param gokartFramePos [m] position of event in go kart frame */
  public static void updateOccurrenceMapLidar(Tensor gokartPose, MapProvider occurrenceMap, double[] gokartFramePos) {
    GeometricLayer gokartToWorldLayer = GeometricLayer.of(Se2Utils.toSE2Matrix(gokartPose));
    Tensor worldCoord = gokartToWorldLayer.toVector(gokartFramePos[0], gokartFramePos[1]);
    occurrenceMap.addValue(worldCoord, 1);
  }

  /** update reactive occurrence map. part of the map which is more then {@link lookBehindDistance} behind go kart is set to zero
   * 
   * @param gokartPose unitless representation
   * @param occurrenceMap
   * @param lookBehindDistance [m] */
  public static void updateReactiveOccurrenceMap(Tensor gokartPose, MapProvider occurrenceMap, double lookBehindDistance) {
    GeometricLayer worldToGokartLayer = GeometricLayer.of(Inverse.of(Se2Utils.toSE2Matrix(gokartPose)));
    double[] mapArray = occurrenceMap.getMapArray();
    for (int i = 0; i < mapArray.length; i++) {
      if (mapArray[i] != 0) {
        double[] worldCoord = occurrenceMap.getCellCoord(i);
        Tensor gokartCoordTensor = worldToGokartLayer.toVector(worldCoord[0], worldCoord[1]);
        double[] gokartCoord = { gokartCoordTensor.Get(0).number().doubleValue(), gokartCoordTensor.Get(1).number().doubleValue() };
        if (gokartCoord[0] < lookBehindDistance) {
          occurrenceMap.setValue(i, 0);
        }
      }
    }
  }

  /** @param currentExpectedPose
   * @param lastExpectedPose
   * @param normalizationMap */
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
          int cellIndexCurrent = normalizationMap.getCellIndex(worldCoordCurrent.Get(0).number().doubleValue(),
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
