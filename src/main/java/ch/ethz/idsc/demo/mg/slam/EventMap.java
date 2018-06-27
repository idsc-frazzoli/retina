// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.GokartToImageInterface;
import ch.ethz.idsc.demo.mg.util.ImageToGokartInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.red.Norm2Squared;

// provides three event maps: occurrence map, normalization map, likelihood map
// TODO probably should be split up into three distinct files
public class EventMap {
  private final MapProvider[] eventMaps = new MapProvider[3];
  private final double lookAheadDistance;
  private final int width;
  private final int height;

  EventMap(PipelineConfig pipelineConfig) {
    for (int i = 0; i < eventMaps.length; i++)
      eventMaps[i] = new MapProvider(pipelineConfig);
    width = pipelineConfig.width.number().intValue();
    height = pipelineConfig.height.number().intValue();
    lookAheadDistance = pipelineConfig.lookAheadDistance.number().doubleValue();
  }

  // TODO precompute gaussian distributions with different variances?
  public void updateOccurrenceMap(double[] gokartFramePos, SlamParticle[] slamParticles) {
    for (int i = 0; i < slamParticles.length; i++) {
      // we generate a Gaussian distribution for each particle. Mean = world coordinate pos, Variance depends on distance to sensor
      // TODO the distance between sensor and each pixel projected into world coord can be precomputed and stored in a lookup table
      Tensor worldCoord = slamParticles[i].getGeometricLayer().toVector(gokartFramePos[0],gokartFramePos[1]);
      // in first edition, we just update the exact position
      eventMaps[0].addValue(worldCoord, slamParticles[i].getParticleLikelihood());
    }
  }

  public void updateNormalizationMap(Tensor currentExpectedPose, Tensor lastExpectedPose, ImageToGokartInterface imageToGokartLookup,
      GokartToImageInterface gokartToImageUtil) {
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
          int cellIndexLast = eventMaps[1].getCellIndex(worldCoordLast.Get(0).number().doubleValue(), worldCoordLast.Get(1).number().doubleValue());
          if (cellIndexLast != eventMaps[1].getNumberOfCells())
            seenCells.add(cellIndexLast);
          // get cell index number for event position using current expected pose
          Tensor worldCoordCurrent = layerCurrent.toVector(gokartCoord[0], gokartCoord[1]);
          int cellIndexCurrent = eventMaps[1].getCellIndex(worldCoordCurrent.Get(0).number().doubleValue(), worldCoordCurrent.Get(1).number().doubleValue());
          if (cellIndexCurrent != eventMaps[1].getNumberOfCells())
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
        double[] cellWorldCoord = eventMaps[1].getCellCoord(cell);
        // transform to go kart frame for current and last go kart pose
        Tensor gokartCoordCurrent = layerCurrentInverse.toVector(cellWorldCoord[0], cellWorldCoord[1]);
        Tensor gokartCoordLast = layerLastInverse.toVector(cellWorldCoord[0], cellWorldCoord[1]);
        // transform to image plane
        Tensor imageCoordCurrent = gokartToImageUtil.gokartToImage(gokartCoordCurrent);
        Tensor imageCoordLast = gokartToImageUtil.gokartToImage(gokartCoordLast);
        // compute norm and add value
        eventMaps[1].addValue(cell, Norm2Squared.between(imageCoordCurrent, imageCoordLast).number().doubleValue());
      }
    }
  }

  public void updateLikelihoodMap() {
    MapProvider.divide(eventMaps[0], eventMaps[1], eventMaps[2]);
  }

  public MapProvider getMap(int mapID) {
    return eventMaps[mapID];
  }
}
