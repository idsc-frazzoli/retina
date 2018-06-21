// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.ImageToWorldInterface;
import ch.ethz.idsc.demo.mg.util.SlamUtil;
import ch.ethz.idsc.demo.mg.util.WorldToImageInterface;
import ch.ethz.idsc.tensor.Tensor;

// provides three event maps: occurrence map, normalization map, likelihood map
// TODO probably should be split up into three distinct files
public class EventMap {
  private final MapProvider[] eventMaps = new MapProvider[3];
  private final int width;
  private final int height;

  EventMap(PipelineConfig pipelineConfig) {
    for (int i = 0; i < 3; i++)
      eventMaps[i] = new MapProvider(pipelineConfig);
    width = pipelineConfig.width.number().intValue();
    height = pipelineConfig.height.number().intValue();
  }

  // TODO precompute gaussian distributions with different variances?
  public void updateOccurrenceMap(double[] gokartFramePos, SlamParticle[] slamParticles) {
    for (int i = 0; i < slamParticles.length; i++) {
      // we generate a Gaussian distribution for each particle. Mean = world coordinate pos, Variance depends on distance to sensor
      // TODO we already compute the world coordinates for all particles in the likelihood update
      Tensor worldCoord = SlamUtil.gokartToWorldTensor(slamParticles[i].getPose(), gokartFramePos);
      // TODO the distance between sensor and each pixel projected into world coord can be precomputed and stored in a lookup table
      // each distribution is weighted with the particle likelihood and then added to the occurrenceMap
      // ..
      // in first try, we just update the exact position
      eventMaps[0].setValue(worldCoord, slamParticles[i].getParticleLikelihood());
    }
  }

  public void updateNormalizationMap(Tensor currentExpectedPose, Tensor lastExpectedPose, ImageToWorldInterface imageToWorldLookup,
      WorldToImageInterface worldToImageUtil) {
    // use of hashset since we want a list of unique cells
    Set<Integer> seenCells = new HashSet<>();
    // find all cells in the map which were seen on the sensor with current or last expected pose
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        double[] gokartCoord = imageToWorldLookup.imageToWorld(i, j);
        // get cell index number for event position using last expected pose
        double[] worldCoordLast = SlamUtil.gokartToWorld(lastExpectedPose, gokartCoord);
        int cellIndexLast = eventMaps[2].getCellIndex(worldCoordLast[0], worldCoordLast[1]);
        if (cellIndexLast != eventMaps[2].getNumberOfCells())
          seenCells.add(cellIndexLast);
        // get cell index number for event position using current expected pose
        double[] worldCoordCurrent = SlamUtil.gokartToWorld(currentExpectedPose, gokartCoord);
        int cellIndexCurrent = eventMaps[2].getCellIndex(worldCoordCurrent[0], worldCoordCurrent[1]);
        if (cellIndexCurrent != eventMaps[2].getNumberOfCells())
          seenCells.add(cellIndexCurrent);
      }
    }
    // for the center of all these cells, compute image plane location for last and current expected pose and compute norm
    synchronized (seenCells) {
      for (Integer cell : seenCells) {
        // find world coordinates of cell middle point
        double[] cellWorldCoord = eventMaps[1].getCellCoord(cell);
        // transform to go kart frame for current and last go kart pose
        double[] cellGokartCoordCurrent = SlamUtil.worldToGokart(currentExpectedPose, cellWorldCoord);
        double[] cellGokartCoordLast = SlamUtil.worldToGokart(lastExpectedPose, cellWorldCoord);
        // transform to image plane
        double[] cellImageCoordCurrent = worldToImageUtil.worldToImage(cellGokartCoordCurrent[0], cellGokartCoordCurrent[1]);
        double[] cellImageCoordLast = worldToImageUtil.worldToImage(cellGokartCoordLast[0], cellGokartCoordLast[1]);
        // compute norm and set value
        double imagePlaneDistance = Math.pow((cellImageCoordCurrent[0] - cellGokartCoordLast[0]), 1)
            + Math.pow((cellImageCoordCurrent[1] - cellImageCoordLast[1]), 1);
        // System.out.println(imagePlaneDistance);
        eventMaps[2].setValue(cell, imagePlaneDistance);
      }
    }
  }

  public void updateLikelihoodMap() {
    eventMaps[2] = MapProvider.divide(eventMaps[0], eventMaps[1]);
  }

  public MapProvider getLikelihoodMap() {
    return eventMaps[2];
  }

  public MapProvider[] getMaps() {
    return eventMaps;
  }
}
