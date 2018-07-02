// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.EventFiltering;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.GokartToImageInterface;
import ch.ethz.idsc.demo.mg.util.ImageToGokartInterface;
import ch.ethz.idsc.demo.mg.util.SlamFileUtil;
import ch.ethz.idsc.demo.mg.util.SlamParticleUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;

// implements the slam algorithm "simultaneous localization and mapping for event-based vision systems"
public class SlamProvider implements DavisDvsListener {
  private final ImageToGokartInterface imageToGokartLookup;
  private final GokartToImageInterface gokartToImageUtil;
  private final GokartPoseInterface gokartOdometryPose;
  private final GokartPoseInterface gokartLidarPose;
  private final EventFiltering eventFiltering;
  private final MapProvider[] eventMaps;
  private final SlamParticle[] slamParticles;
  private final double linVelStd;
  private final double angVelStd;
  private final double alpha;
  private final int numOfPart;
  private final int relevantParticles;
  private final SlamVelocityEstimator velEstimator;
  private final SlamEstimatedPose estimatedPose;
  private final SlamEstimatedPose estimatedPose2;
  private final boolean localizationMode;
  private final double lookAheadDistance;
  private final double dT = 0.1; // [s]
  private final int normalizationUpdateRate;
  // --
  private double lastNormalizationTimeStamp;
  private double lastPropagationTimeStamp;
  private Tensor lastExpectedPose;

  SlamProvider(PipelineConfig pipelineConfig, GokartPoseInterface gokartOdometryPose, GokartPoseInterface gokartLidarPose) {
    imageToGokartLookup = pipelineConfig.createImageToGokartUtilLookup();
    gokartToImageUtil = pipelineConfig.createGokartToImageUtil();
    this.gokartOdometryPose = gokartOdometryPose;
    this.gokartLidarPose = gokartLidarPose;
    eventFiltering = new EventFiltering(pipelineConfig);
    eventMaps = new MapProvider[3];
    for (int i = 0; i < 3; i++)
      eventMaps[i] = new MapProvider(pipelineConfig);
    numOfPart = pipelineConfig.numberOfParticles.number().intValue();
    relevantParticles = pipelineConfig.relevantParticles.number().intValue();
    slamParticles = new SlamParticle[numOfPart];
    for (int i = 0; i < numOfPart; i++)
      slamParticles[i] = new SlamParticle();
    linVelStd = pipelineConfig.linVelStandardDeviation.number().doubleValue();
    angVelStd = pipelineConfig.angVelStandardDeviation.number().doubleValue();
    alpha = pipelineConfig.alpha.number().doubleValue();
    normalizationUpdateRate = pipelineConfig.normalizationUpdateRate.number().intValue() * 1000;
    lookAheadDistance = pipelineConfig.lookAheadDistance.number().doubleValue();
    estimatedPose = new SlamEstimatedPose();
    estimatedPose2 = new SlamEstimatedPose();
    localizationMode = pipelineConfig.localizationMode;
    velEstimator = new SlamVelocityEstimator();
  }

  public void initialize(Tensor pose, double timeStamp, String imagePrefix) {
    SlamParticleUtil.setInitialDistribution(slamParticles, pose);
    lastExpectedPose = pose;
    velEstimator.initialize(pose, timeStamp);
    estimatedPose.setPose(pose);
    estimatedPose2.setPose(pose);
    lastNormalizationTimeStamp = timeStamp;
    lastPropagationTimeStamp = timeStamp;
    if (localizationMode) {
      double[] mapArray = new double[eventMaps[0].getNumberOfCells()];
      SlamFileUtil.loadFromCSV(SlamFileLocations.recordedMaps(imagePrefix), mapArray);
      eventMaps[0].setMapArray(mapArray);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (eventFiltering.filterPipeline(davisDvsEvent)) {
      double currentTimeStamp = davisDvsEvent.time / 1000000.0;
      double[] eventGokartFrame = imageToGokartLookup.imageToGokart(davisDvsEvent.x, davisDvsEvent.y);
      if ((currentTimeStamp - lastPropagationTimeStamp) > dT) {
        SlamParticleUtil.propagateStateEstimate(slamParticles, velEstimator.getLinVelNorm2(), linVelStd, velEstimator.getAngVelDouble(), angVelStd, dT);
        lastPropagationTimeStamp = currentTimeStamp;
        velEstimator.updateEstimate(estimatedPose2.getPose(), currentTimeStamp);
        SlamParticleUtil.resampleParticles(slamParticles);
        // printStatusInfo();
      }
      if (eventGokartFrame[0] < lookAheadDistance) {
        SlamParticleUtil.updateLikelihoods(slamParticles, eventMaps[0], eventGokartFrame, alpha);
        // SlamMapUtil.updateOccurrenceMap(slamParticles, eventMaps[0], eventGokartFrame, relevantParticles);
      }
      // if ((currentTimeStamp - lastNormalizationTimeStamp) > normalizationUpdateRate) {
      // SlamMapUtil.updateNormalizationMap(gokartLidarPose.getPose(), lastExpectedPose, eventMaps[1], imageToGokartLookup, gokartToImageUtil, 240, 180,
      // lookAheadDistance);
      // lastExpectedPose = gokartLidarPose.getPose();
      // printStatusInfo();
      // lastNormalizationTimeStamp = currentTimeStamp;
      // MapProvider.divide(eventMaps[0], eventMaps[1], eventMaps[2]);
      // }
      // update GokartPoseInterface
      estimatedPose.setPose(SlamParticleUtil.getAveragePose(slamParticles,2));
      estimatedPose2.setPose(gokartLidarPose.getPose());
    }
  }

  // provides interface for use of SLAM pose in other modules
  public GokartPoseInterface getPoseInterface() {
    return estimatedPose;
  }
  
  public SlamParticle[] getParticles() {
    return slamParticles;
  }

  // mapID: 0 == occurrence map, 1 == normalization map, 2 == likelihood map
  public MapProvider getMap(int mapID) {
    return eventMaps[mapID];
  }

  private void printStatusInfo() {
    System.out.println("**** new status info **********");
    for (int i = 0; i < slamParticles.length; i++) {
      System.out.println("Particle likelihood " + slamParticles[i].getParticleLikelihood());
    }
  }
}
