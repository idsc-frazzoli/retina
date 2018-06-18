// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.ImageToWorldLookup;
import ch.ethz.idsc.tensor.Tensor;

// provides three event maps: occurrence map, normalization map, likelihood map
public class EventMap {
  private final MapProvider[] eventMaps = new MapProvider[3];
  
  EventMap(PipelineConfig pipelineConfig){
    for(int i=0;i<3;i++)
        eventMaps[i] = new MapProvider(pipelineConfig);
  }
  
  // TODO precompute gaussian distributions with different variances?
  public void updateOccurrenceMap(double[] gokartFramePos, SlamParticle[] slamParticles) {
    for (int i = 0; i < slamParticles.length; i++) {
      // we generate a Gaussian distribution for each particle. Mean = world coordinate pos, Variance depends on distance to sensor 
      // TODO we already compute the world coordinates for all particles in the likelihood update
      Tensor worldCoord = slamParticles[i].getWorldCoord(gokartFramePos);
      // TODO the distance between sensor and each pixel projected into world coord can be precomputed and stored in a lookup table
      // double distanceToEvent = Math.sqrt(gokartFramePos[0] * gokartFramePos[0] + gokartFramePos[1] * gokartFramePos[1]);
      // each distribution is weighted with the particle likelihood and then added to the occurrenceMap
      // ..
      // in first try, we just update the exact position
      eventMaps[0].setValue(worldCoord, 1);
    }
  }
  
  public void updateNormalizationMap(Tensor currentExpectedPose, Tensor lastExpectedPose, ImageToWorldLookup imageToWorldLookup) {
    // find all cells in the map which were seen on the sensor with current or last expected pose
    // for the center of all these cells, compute image plane location for last and current expected pose and compute norm
    // ..
    // NOTE: map location close to the sensor will have larger movements on the image plane than far away locations
    // therefore, we normalize the fact that far away features do not generate as many events
  }
  
  public void updateLikelihoodMap() {
    eventMaps[2] = MapProvider.divide(eventMaps[0], eventMaps[1]);
  }
  
  public MapProvider getLikelihoodMap() {
    return eventMaps[2];
  }
}
