// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.pipeline.EventFiltering;
import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.slam.WayPoint;
import ch.ethz.idsc.demo.mg.util.GokartToImageInterface;
import ch.ethz.idsc.demo.mg.util.ImageToGokartInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;

/** implements the SLAM algorithm
 * "simultaneous localization and mapping for event-based vision systems" */
public class SlamProvider implements DavisDvsListener {
  private final ImageToGokartInterface imageToGokartLookup;
  private final GokartToImageInterface gokartToImageUtil;
  private final GokartPoseInterface gokartLidarPose;
  private final GokartPoseOdometryDemo gokartOdometry;
  private final EventFiltering eventFiltering;
  private final SlamParticle[] slamParticles;
  private final SlamLocalizationStep slamLocalizationStep;
  private final SlamMappingStep slamMappingStep;
  private final SlamMapProcessing slamWayPoints;
  private final SlamTrajectoryPlanning slamTrajectoryPlanning;
  private final boolean lidarMappingMode;
  private final int numOfPart;
  private boolean isInitialized;

  public SlamProvider(SlamConfig slamConfig, GokartPoseOdometryDemo gokartOdometry, GokartPoseInterface gokartLidarPose) {
    imageToGokartLookup = slamConfig.davisConfig.createImageToGokartUtilLookup();
    gokartToImageUtil = slamConfig.davisConfig.createGokartToImageUtil();
    this.gokartLidarPose = gokartLidarPose;
    this.gokartOdometry = gokartOdometry;
    eventFiltering = new EventFiltering(slamConfig.davisConfig);
    slamLocalizationStep = new SlamLocalizationStep(slamConfig);
    slamMappingStep = new SlamMappingStep(slamConfig);
    slamWayPoints = new SlamMapProcessing(slamConfig);
    slamTrajectoryPlanning = new SlamTrajectoryPlanning(slamConfig, slamLocalizationStep.getPoseInterface());
    lidarMappingMode = slamConfig.lidarMappingMode;
    numOfPart = slamConfig.numberOfParticles.number().intValue();
    slamParticles = new SlamParticle[numOfPart];
    for (int i = 0; i < numOfPart; i++)
      slamParticles[i] = new SlamParticle();
  }

  public void initialize(Tensor pose, double timeStamp) {
    slamLocalizationStep.initialize(slamParticles, pose, timeStamp);
    slamMappingStep.initialize(timeStamp);
    slamWayPoints.initialize(timeStamp);
    slamTrajectoryPlanning.initialize(timeStamp);
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
      if (lidarMappingMode) {
        slamLocalizationStep.setPose(gokartLidarPose.getPose());
        slamMappingStep.mappingStepWithLidar(gokartLidarPose.getPose(), eventGokartFrame, currentTimeStamp);
      } else {
        slamLocalizationStep.localizationStep(slamParticles, slamMappingStep.getMap(0), gokartOdometry.getVelocity(), eventGokartFrame, currentTimeStamp);
        slamMappingStep.mappingStep(slamParticles, slamLocalizationStep.getPoseInterface().getPose(), eventGokartFrame, currentTimeStamp);
        slamWayPoints.mapPostProcessing(slamMappingStep.getMap(0), currentTimeStamp);
        slamTrajectoryPlanning.computeTrajectory(slamWayPoints.getWorldWayPoints(), currentTimeStamp);
      }
    }
  }

  public GokartPoseInterface getPoseInterface() {
    return slamLocalizationStep.getPoseInterface();
  }

  public SlamParticle[] getParticles() {
    return slamParticles;
  }

  public Mat getProcessedMat() {
    return slamWayPoints.getProcessedMat();
  }

  public List<WayPoint> getWayPoints() {
    return slamTrajectoryPlanning.getWayPoints();
  }

  // mapID: 0 == occurrence map, 1 == normalization map, 2 == likelihood map
  public MapProvider getMap(int mapID) {
    return slamMappingStep.getMap(mapID);
  }
}
