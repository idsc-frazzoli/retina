// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.Arrays;

import ch.ethz.idsc.demo.mg.pipeline.EventFiltering;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.GokartToImageInterface;
import ch.ethz.idsc.demo.mg.util.ImageToGokartInterface;
import ch.ethz.idsc.demo.mg.util.SlamFileUtil;
import ch.ethz.idsc.demo.mg.util.SlamMapUtil;
import ch.ethz.idsc.demo.mg.util.SlamParticleUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;

// implements the slam algorithm "simultaneous localization and mapping for event-based vision systems"
// TODO finish reorganization with SlamLocalizationStep and slamMappingStep
public class SlamProvider implements DavisDvsListener {
  private final ImageToGokartInterface imageToGokartLookup;
  private final GokartToImageInterface gokartToImageUtil;
  private final GokartPoseInterface gokartOdometryPose;
  private final GokartPoseInterface gokartLidarPose;
  private final EventFiltering eventFiltering;
  private final MapProvider[] eventMaps;
  private final SlamParticle[] slamParticles;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;
  private final double alpha;
  private final int numOfPart;
  private final int relevantParticles;
  private final SlamEstimatedPose estimatedPose;
  private final String imagePrefix;
  private final boolean localizationMode;
  private final double lookAheadDistance;
  private final double dT = 0.001; // [s]
  private final double normalizationUpdateRate;
  // --
  private double lastPropagationTimeStamp;
  private double lastResampleTimeStamp;
  private double lastNormalizationTimeStamp;
  private boolean isInitialized;
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
    linVelAvg = pipelineConfig.linVelAvg.number().doubleValue();
    linVelStd = pipelineConfig.linVelStd.number().doubleValue();
    angVelStd = pipelineConfig.angVelStd.number().doubleValue();
    alpha = pipelineConfig.alpha.number().doubleValue();
    normalizationUpdateRate = pipelineConfig.normalizationUpdateRate.number().doubleValue();
    lookAheadDistance = pipelineConfig.lookAheadDistance.number().doubleValue();
    estimatedPose = new SlamEstimatedPose();
    imagePrefix = pipelineConfig.logFileName;
    localizationMode = pipelineConfig.localizationMode;
  }

  public void initialize(Tensor pose, double timeStamp) {
    SlamParticleUtil.setInitialDistribution(slamParticles, pose, linVelAvg, linVelStd, angVelStd);
    lastExpectedPose = pose;
    estimatedPose.setPose(pose);
    lastNormalizationTimeStamp = timeStamp;
    lastPropagationTimeStamp = timeStamp;
    lastResampleTimeStamp = timeStamp;
    if (localizationMode) {
      double[] mapArray = new double[eventMaps[0].getNumberOfCells()];
      SlamFileUtil.loadFromCSV(SlamFileLocations.recordedMaps(imagePrefix), mapArray);
      eventMaps[0].setMapArray(mapArray);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!isInitialized) {
      initialize(gokartLidarPose.getPose(), davisDvsEvent.time / 1000000.0);
      isInitialized = true;
    }
    if (eventFiltering.filterPipeline(davisDvsEvent)) {
      double currentTimeStamp = davisDvsEvent.time / 1000000.0;
      double[] eventGokartFrame = imageToGokartLookup.imageToGokart(davisDvsEvent.x, davisDvsEvent.y);
      if ((currentTimeStamp - lastPropagationTimeStamp) > dT) {
        SlamParticleUtil.propagateStateEstimate(slamParticles, currentTimeStamp - lastPropagationTimeStamp);
        lastPropagationTimeStamp = currentTimeStamp;
      }
      // TODO probably better resample on event count basis
      if ((currentTimeStamp - lastResampleTimeStamp) > 10 * dT) {
        // printStatusInfo();
        SlamParticleUtil.resampleParticles(slamParticles, currentTimeStamp - lastResampleTimeStamp);
        lastResampleTimeStamp = currentTimeStamp;
      }
      if (eventGokartFrame[0] < lookAheadDistance) {
        SlamParticleUtil.updateLikelihoods(slamParticles, eventMaps[0], eventGokartFrame, alpha);
        if (!localizationMode)
          SlamMapUtil.updateOccurrenceMapParticles(slamParticles, eventMaps[0], eventGokartFrame, relevantParticles);
      }
      if ((currentTimeStamp - lastNormalizationTimeStamp) > normalizationUpdateRate) {
        // System.out.println("**velocities**");
        // for(int i=0;i<slamParticles.length;i++) {
        // System.out.println(slamParticles[i].getLinVelDouble());
        // }
        // SlamMapUtil.updateNormalizationMap(gokartLidarPose.getPose(), lastExpectedPose, eventMaps[1], imageToGokartLookup, gokartToImageUtil, 240, 180,
        // lookAheadDistance);
        // lastExpectedPose = gokartLidarPose.getPose();
        // printStatusInfo();
        // lastNormalizationTimeStamp = currentTimeStamp;
        // MapProvider.divide(eventMaps[0], eventMaps[1], eventMaps[2]);
      }
      // update GokartPoseInterface
       estimatedPose.setPose(SlamParticleUtil.getAveragePose(slamParticles, 1));
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
    Arrays.sort(slamParticles, SlamParticleUtil.SlamCompare);
    System.out.println("**** new status info **********");
    for (int i = 0; i < slamParticles.length; i++) {
      System.out.println("Particle likelihood " + slamParticles[i].getParticleLikelihood());
    }
  }
}
