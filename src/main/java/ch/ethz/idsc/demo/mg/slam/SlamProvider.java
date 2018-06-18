// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.ImageToWorldLookup;
import ch.ethz.idsc.demo.mg.util.WorldToImageUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;

// implements the slam algorithm "simultaneous localization and mapping for event-based vision systems"
public class SlamProvider implements DavisDvsListener {
  // camera utilities
  private final ImageToWorldLookup imageToWorldLookup;
  private final WorldToImageUtil worldToImageUtil;
  // odometry
  private GokartPoseOdometry gokartPoseOdometry;
  // maps
  private final EventMap eventMaps;
  // particles
  private final SlamParticleSet slamParticleSet;
  // further fields
  private Tensor lastExpectedPose;
  private int lastTimeStamp;

  SlamProvider(PipelineConfig pipelineConfig, GokartPoseOdometry gokartPoseOdometry) {
    imageToWorldLookup = pipelineConfig.createImageToWorldUtilLookup();
    worldToImageUtil = pipelineConfig.createWorldToImageUtil();
    this.gokartPoseOdometry = gokartPoseOdometry;
    eventMaps = new EventMap(pipelineConfig);
    // TODO initial lidar pose for initialization required
    slamParticleSet = new SlamParticleSet(pipelineConfig);
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    // we map the event onto go kart coordinates since this is gonna be required multiple times
    double[] eventGokartFrame = imageToWorldLookup.imageToWorld(davisDvsEvent.x, davisDvsEvent.y);
    // localization step:
    // state estimate propagation
    // slamParticleSet.propagateStateEstimate();
    slamParticleSet.setPose(gokartPoseOdometry.getPose()); // for testing
    // state likelihoods update
    slamParticleSet.updateStateLikelihoods(eventGokartFrame, eventMaps.getLikelihoodMap());
    // mapping step:
    // occurrence map update
    eventMaps.updateOccurrenceMap(eventGokartFrame, slamParticleSet.getParticles());
    // normalization map update
    // this does not need to be done for every event --> choose some threshold maybe 50ms
    if (davisDvsEvent.time - lastTimeStamp > 1000) {
      eventMaps.updateNormalizationMap(slamParticleSet.getExpectedPose(), lastExpectedPose, imageToWorldLookup);
      lastExpectedPose = slamParticleSet.getExpectedPose();
      lastTimeStamp = davisDvsEvent.time;
    }
    // likelihood update
    eventMaps.updateLikelihoodMap();
  }
}
