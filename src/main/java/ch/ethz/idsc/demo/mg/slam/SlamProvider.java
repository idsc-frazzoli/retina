// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.ImageToGokartInterface;
import ch.ethz.idsc.demo.mg.util.GokartToImageInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;

// implements the slam algorithm "simultaneous localization and mapping for event-based vision systems"
public class SlamProvider implements DavisDvsListener {
  // camera utilities
  private final ImageToGokartInterface imageToWorldLookup;
  private final GokartToImageInterface worldToImageUtil;
  // odometry and lidar pose
  private final GokartPoseInterface gokartOdometryPose;
  private final GokartPoseInterface gokartLidarPose;
  // maps
  private final EventMap eventMaps;
  // particles
  private final SlamParticleSet slamParticleSet;
  // further fields
  private final double lookAheadDistance;
  private SlamEstimatedPose estimatedPose;
  private boolean isInitialized = false;
  private int lastTimeStamp;
  private Tensor lastExpectedPose;

  SlamProvider(PipelineConfig pipelineConfig, GokartPoseInterface gokartOdometryPose, GokartPoseInterface gokartLidarPose) {
    lookAheadDistance = pipelineConfig.lookAheadDistance.number().doubleValue();
    imageToWorldLookup = pipelineConfig.createImageToGokartUtilLookup();
    worldToImageUtil = pipelineConfig.createGokartToImageUtil();
    eventMaps = new EventMap(pipelineConfig);
    this.gokartOdometryPose = gokartOdometryPose;
    this.gokartLidarPose = gokartLidarPose;
    // TODO initial lidar pose for initialization required
    slamParticleSet = new SlamParticleSet(pipelineConfig);
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if(!isInitialized) {
      lastTimeStamp = davisDvsEvent.time;
      isInitialized = true;
    }
    // we map the event onto go kart coordinates since this is gonna be required multiple times
    double[] eventGokartFrame = imageToWorldLookup.imageToGokart(davisDvsEvent.x, davisDvsEvent.y);
    // localization step:
    // state estimate propagation
    // slamParticleSet.propagateStateEstimate();
    slamParticleSet.setPose(gokartLidarPose.getPose()); // for testing
    // state likelihoods update
    // slamParticleSet.updateStateLikelihoods(eventGokartFrame, eventMaps.getLikelihoodMap());
    // mapping step:
    // occurrence map update
    // include lookahead chop off
    if (eventGokartFrame[0] < lookAheadDistance) {
      eventMaps.updateOccurrenceMap(eventGokartFrame, slamParticleSet.getParticles());
    }
    // normalization map update
     if (davisDvsEvent.time - lastTimeStamp > 5000) {
     eventMaps.updateNormalizationMap(slamParticleSet.getExpectedPose(), lastExpectedPose, imageToWorldLookup, worldToImageUtil);
     lastExpectedPose = slamParticleSet.getExpectedPose();
     lastTimeStamp = davisDvsEvent.time;
     }
    // update GokartPoseInterface
    // estimatedPose.setPose(slamParticleSet.getExpectedPose());
    // likelihood update
    // eventMaps.updateLikelihoodMap();
    // particle resampling??
  }

  // for visualization
  public GokartPoseInterface getPoseInterface() {
    return estimatedPose;
  }

  // mapID: 0 == occurrence map, 1 == normalization map, 2 == likelihood map
  public MapProvider getMap(int mapID) {
    return eventMaps.getMap(mapID);
  }
}
