// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.GokartToImageInterface;
import ch.ethz.idsc.demo.mg.util.ImageToGokartInterface;
import ch.ethz.idsc.demo.mg.util.SlamMapUtil;
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
  private final SlamParticle[] slamParticles;
  private final int numOfPart;
  private final double linVelStd;
  private final double angVelStd;
  private final double alpha;
  // velocity
  private final SlamVelocityEstimator velEstimator;
  // further fields
  private final SlamEstimatedPose estimatedPose;
  private final double lookAheadDistance;
  private final Scalar dT = RealScalar.of(0.01); // [s]
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
    numOfPart = pipelineConfig.numberOfParticles.number().intValue();
    slamParticles = new SlamParticle[numOfPart];
    for (int i = 0; i < numOfPart; i++)
      slamParticles[i] = new SlamParticle();
    linVelStd = pipelineConfig.linVelStandardDeviation.number().doubleValue();
    angVelStd = pipelineConfig.angVelStandardDeviation.number().doubleValue();
    alpha = pipelineConfig.alpha.number().doubleValue();
    estimatedPose = new SlamEstimatedPose();
    velEstimator = new SlamVelocityEstimator();
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
      SlamParticleUtil.propagateStateEstimate(slamParticles, velEstimator.getLinVelNorm2(), linVelStd, angVelStd, dT);
      lastPropagationTimeStamp = davisDvsEvent.time;
      double currentTimeStamp = davisDvsEvent.time / 1000000.0;
      velEstimator.updateEstimate(estimatedPose.getPose(), currentTimeStamp);
      // System.out.println(velocityEstimator.getVelocity());
      SlamParticleUtil.resampleParticles(slamParticles);
      // printStatusInfo();
    }
    if (eventGokartFrame[0] < lookAheadDistance) {
      // state likelihoods update
      SlamParticleUtil.updateLikelihoods(slamParticles, eventMaps.getMap(0), eventGokartFrame, alpha);
      // mapping step:
      // occurrence map update
      SlamMapUtil.updateOccurrenceMap(slamParticles, eventMaps.getMap(0), eventGokartFrame);
    }
    // normalization map update
    if ((davisDvsEvent.time - lastNormalizationTimeStamp) > normalizationUpdateRate) {
      // eventMaps.updateNormalizationMap(gokartLidarPose.getPose(), lastExpectedPose, imageToGokartLookup, gokartToImageUtil);
      // lastExpectedPose = gokartLidarPose.getPose();
      // System.out.println(estimatedPose.getPose());
      // printStatusInfo();
      lastNormalizationTimeStamp = davisDvsEvent.time;
      // MapProvider.divide(eventMaps[0], eventMaps[1], eventMaps[2]);
    }
    // update GokartPoseInterface
    estimatedPose.setPose(SlamParticleUtil.getAveragePose(slamParticles));
  }

  public void initializePose(Tensor pose, double timeStamp) {
    lastExpectedPose = pose;
    SlamParticleUtil.setInitialDistribution(slamParticles, pose);
    velEstimator.initialize(pose, timeStamp);
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
    for (int i = 0; i < slamParticles.length; i++) {
      System.out.println("Particle likelihood " + slamParticles[i].getParticleLikelihood());
    }
  }
}
