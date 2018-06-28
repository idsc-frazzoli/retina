// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.GokartToImageInterface;
import ch.ethz.idsc.demo.mg.util.ImageToGokartInterface;
import ch.ethz.idsc.demo.mg.util.SlamParticleUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// implements the slam algorithm "simultaneous localization and mapping for event-based vision systems"
public class SlamProvider implements DavisDvsListener {
  // camera utilities
  private final ImageToGokartInterface imageToGokartLookup;
  private final GokartToImageInterface gokartToImageUtil;
  // odometry and lidar pose
  private final GokartPoseInterface gokartOdometryPose;
  private final GokartPoseInterface gokartLidarPose;
  // maps
  private final EventMap eventMaps;
  // particles
  private final SlamParticleSet slamParticleSet;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;
  // velocity
  private final SlamVelocityEstimator velocityEstimator;
  // further fields
  private final SlamEstimatedPose estimatedPose;
  private final double lookAheadDistance;
  private final Scalar dT = RealScalar.of(0.1); // [s]
  private final int normalizationUpdateRate;
  private boolean isInitialized = false;
  private int lastNormalizationTimeStamp;
  private int lastPropagationTimeStamp;
  private Tensor lastExpectedPose;

  SlamProvider(PipelineConfig pipelineConfig, GokartPoseInterface gokartOdometryPose, GokartPoseInterface gokartLidarPose) {
    normalizationUpdateRate = pipelineConfig.normalizationUpdateRate.number().intValue() * 1000;
    lookAheadDistance = pipelineConfig.lookAheadDistance.number().doubleValue();
    imageToGokartLookup = pipelineConfig.createImageToGokartUtilLookup();
    gokartToImageUtil = pipelineConfig.createGokartToImageUtil();
    eventMaps = new EventMap(pipelineConfig);
    slamParticleSet = new SlamParticleSet(pipelineConfig);
    linVelAvg = pipelineConfig.linVelAverage.number().doubleValue();
    linVelStd = pipelineConfig.linVelStandardDeviation.number().doubleValue();
    angVelStd = pipelineConfig.angVelStandardDeviation.number().doubleValue();
    estimatedPose = new SlamEstimatedPose();
    velocityEstimator = new SlamVelocityEstimator();
    this.gokartOdometryPose = gokartOdometryPose;
    this.gokartLidarPose = gokartLidarPose;
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!isInitialized) {
      lastNormalizationTimeStamp = davisDvsEvent.time;
      lastPropagationTimeStamp = davisDvsEvent.time;
      isInitialized = true;
    }
    double[] eventGokartFrame = imageToGokartLookup.imageToGokart(davisDvsEvent.x, davisDvsEvent.y);
    // localization step:
    // state estimate propagation
    if ((davisDvsEvent.time - lastPropagationTimeStamp) > dT.number().doubleValue() * 1000000) {
      SlamParticleUtil.propagateStateEstimate(linVelAvg, linVelStd, angVelStd, dT, slamParticleSet.getParticles());
      slamParticleSet.updateStateLikelihoods(eventGokartFrame, eventMaps.getMap(0));
      lastPropagationTimeStamp = davisDvsEvent.time;
      double currentTimeStamp = davisDvsEvent.time / 1000000.0;
      velocityEstimator.updateEstimate(estimatedPose.getPose(), currentTimeStamp);
      // below for testing accuracy of Se2Integrator
      // System.out.println(Norm2Squared.ofVector(velocityEstimator.getVelocity()));
      SlamParticleUtil.resampleParticles(slamParticleSet.getParticles());
      // printStatusInfo();
    }
    // slamParticleSet.setPose(gokartLidarPose.getPose()); // for testing
    if (eventGokartFrame[0] < lookAheadDistance) {
      // state likelihoods update
      // mapping step:
      // occurrence map update
      eventMaps.updateOccurrenceMap(eventGokartFrame, slamParticleSet.getParticles());
    }
    // normalization map update
    if ((davisDvsEvent.time - lastNormalizationTimeStamp) > normalizationUpdateRate) {
      // eventMaps.updateNormalizationMap(gokartLidarPose.getPose(), lastExpectedPose, imageToGokartLookup, gokartToImageUtil);
      // lastExpectedPose = gokartLidarPose.getPose();
      // System.out.println(estimatedPose.getPose());
      lastNormalizationTimeStamp = davisDvsEvent.time;
      // eventMaps.updateLikelihoodMap();
    }
    // update GokartPoseInterface
    estimatedPose.setPose(slamParticleSet.getExpectedPose());
    // particle resampling
  }

  public void initializePose(Tensor pose, double timeStamp) {
    lastExpectedPose = pose;
    SlamParticleUtil.setInitialDistribution(pose, slamParticleSet.getParticles());
    velocityEstimator.initialize(pose, timeStamp);
    estimatedPose.setPose(pose);
  }

  // provides interface for use of SLAM pose in other modules
  public GokartPoseInterface getPoseInterface() {
    return estimatedPose;
  }

  // mapID: 0 == occurrence map, 1 == normalization map, 2 == likelihood map
  public MapProvider getMap(int mapID) {
    return eventMaps.getMap(mapID);
  }

  private void printStatusInfo() {
    System.out.println("**** new status info **********");
    for (int i = 0; i < slamParticleSet.getNumberOfParticles(); i++) {
      System.out.println("Particle likelihood " + slamParticleSet.getParticle(i).getParticleLikelihood());
    }
  }
}
