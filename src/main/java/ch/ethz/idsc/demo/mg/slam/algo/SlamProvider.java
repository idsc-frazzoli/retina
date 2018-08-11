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
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;

/** implementation of the SLAM algorithm
 * "simultaneous localization and mapping for event-based vision systems"
 * by David Weikersdorfer, Raoul Hoffmann, and Joerg Conradt
 * https://mediatum.ub.tum.de/doc/1191908/1191908.pdf */
public class SlamProvider implements DavisDvsListener {
  private final ImageToGokartInterface imageToGokartInterface;
  // TODO MG gokartToImageUtil not used: remove? (also in the constructor)
  // private final GokartToImageInterface gokartToImageUtil;
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
  // --
  public double timeSum;
  public double eventCount;
  private double initTimeStamp;

  public SlamProvider(SlamConfig slamConfig, GokartPoseOdometryDemo gokartOdometry, GokartPoseInterface gokartLidarPose) {
    imageToGokartInterface = slamConfig.davisConfig.createImageToGokartUtilLookup();
    // gokartToImageUtil = slamConfig.davisConfig.createGokartToImageUtil();
    this.gokartLidarPose = gokartLidarPose;
    this.gokartOdometry = gokartOdometry;
    eventFiltering = new EventFiltering(slamConfig.davisConfig);
    slamLocalizationStep = new SlamLocalizationStep(slamConfig);
    slamMappingStep = new SlamMappingStep(slamConfig);
    slamWayPoints = new SlamMapProcessing(slamConfig);
    slamTrajectoryPlanning = new SlamTrajectoryPlanning(slamConfig, slamLocalizationStep.getSlamEstimatedPose());
    lidarMappingMode = slamConfig.lidarMappingMode;
    numOfPart = slamConfig.numberOfParticles.number().intValue();
    slamParticles = new SlamParticle[numOfPart];
    for (int i = 0; i < numOfPart; i++)
      slamParticles[i] = new SlamParticle();
  }

  public void initialize(Tensor pose, double timeStamp) {
    gokartOdometry.setPose(pose);
    slamLocalizationStep.initialize(slamParticles, pose, timeStamp);
    slamMappingStep.initialize(timeStamp);
    slamWayPoints.initialize(timeStamp);
    slamTrajectoryPlanning.initialize(timeStamp);
    initTimeStamp = timeStamp;
    isInitialized = true;
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!isInitialized) {
      if (gokartLidarPose.getPose() != GokartPoseLocal.INSTANCE.getPose())
        initialize(gokartLidarPose.getPose(), davisDvsEvent.time / 1000000.0);
    } else {
      if (eventFiltering.filterPipeline(davisDvsEvent)) {
        double currentTimeStamp = davisDvsEvent.time / 1000000.0;
        double[] eventGokartFrame = imageToGokartInterface.imageToGokart(davisDvsEvent.x, davisDvsEvent.y);
        if (lidarMappingMode) {
          slamLocalizationStep.setPose(gokartLidarPose.getPose());
          slamMappingStep.mappingStepWithLidar(slamLocalizationStep.getSlamEstimatedPose().getPoseUnitless(), eventGokartFrame, currentTimeStamp);
        } else {
          Stopwatch stopwatch = Stopwatch.started();
          slamLocalizationStep.localizationStep(slamParticles, slamMappingStep.getMap(0), gokartOdometry.getVelocity(), eventGokartFrame, currentTimeStamp);
          slamMappingStep.mappingStep(slamParticles, slamLocalizationStep.getSlamEstimatedPose().getPoseUnitless(), eventGokartFrame, currentTimeStamp);
          slamWayPoints.mapPostProcessing(slamMappingStep.getMap(0), currentTimeStamp);
          slamTrajectoryPlanning.computeTrajectory(slamWayPoints.getWorldWayPoints(), currentTimeStamp);
          if (stopwatch.display_seconds() > 0.01)
            System.out.println(stopwatch.display_seconds());
        }
        eventCount++;
        if (currentTimeStamp - initTimeStamp > 10) {
          System.out.println(eventFiltering.getFilteredPercentage());
          System.out.println("avg time is " + timeSum / eventCount);
          initTimeStamp += 10;
        }
      }
    }
  }

  public GokartPoseInterface getPoseInterface() {
    return slamLocalizationStep.getSlamEstimatedPose();
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

  public boolean getIsInitialized() {
    return isInitialized;
  }
}
