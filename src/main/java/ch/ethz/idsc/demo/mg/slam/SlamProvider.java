// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.ImageToWorldInterface;
import ch.ethz.idsc.demo.mg.util.WorldToImageInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// implements the slam algorithm "simultaneous localization and mapping for event-based vision systems"
public class SlamProvider implements DavisDvsListener {
  // camera utilities
  private final ImageToWorldInterface imageToWorldLookup;
  private final WorldToImageInterface worldToImageUtil;
  // odometry and lidar pose
  private final GokartPoseInterface gokartOdometryPose;
  private final GokartPoseInterface gokartLidarPose;
  // maps
  private final EventMap eventMaps;
  // particles
  private final SlamParticleSet slamParticleSet;
  // further fields
  private Tensor lastExpectedPose = Tensors.of(Quantity.of(0, SI.METER), Quantity.of(0, SI.METER), DoubleScalar.of(0));
  private int lastTimeStamp;
  private boolean isInitialized = false;

  SlamProvider(PipelineConfig pipelineConfig, GokartPoseInterface gokartOdometryPose, GokartPoseInterface gokartLidarPose) {
    imageToWorldLookup = pipelineConfig.createImageToWorldUtilLookup();
    worldToImageUtil = pipelineConfig.createWorldToImageUtil();
    eventMaps = new EventMap(pipelineConfig);
    this.gokartOdometryPose = gokartOdometryPose;
    this.gokartLidarPose = gokartLidarPose;
    // TODO initial lidar pose for initialization required
    slamParticleSet = new SlamParticleSet(pipelineConfig);
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!isInitialized) {
      lastTimeStamp = davisDvsEvent.time;
      isInitialized = true;
    }
    // we map the event onto go kart coordinates since this is gonna be required multiple times
    double[] eventGokartFrame = imageToWorldLookup.imageToWorld(davisDvsEvent.x, davisDvsEvent.y);
    // localization step:
    // state estimate propagation
    // slamParticleSet.propagateStateEstimate();
    slamParticleSet.setPose(gokartLidarPose.getPose()); // for testing
    // state likelihoods update
    slamParticleSet.updateStateLikelihoods(eventGokartFrame, eventMaps.getLikelihoodMap());
    // mapping step:
    // occurrence map update
    eventMaps.updateOccurrenceMap(eventGokartFrame, slamParticleSet.getParticles());
    // normalization map update
    // this does not need to be done for every event --> choose some threshold maybe 50ms
    if (davisDvsEvent.time - lastTimeStamp > 5000) {
      eventMaps.updateNormalizationMap(slamParticleSet.getExpectedPose(), lastExpectedPose, imageToWorldLookup, worldToImageUtil);
      lastExpectedPose = slamParticleSet.getExpectedPose();
      lastTimeStamp = davisDvsEvent.time;
    }
    // likelihood update
    eventMaps.updateLikelihoodMap();
    // particle resampling??
  }
}
