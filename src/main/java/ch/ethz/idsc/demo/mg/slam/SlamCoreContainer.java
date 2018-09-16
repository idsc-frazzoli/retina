// code by mg
package ch.ethz.idsc.demo.mg.slam;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.retina.util.io.PrimitivesIO;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** container for the objects that are passed between different modules of the SLAM algorithm */
public class SlamCoreContainer implements GokartPoseUnitlessInterface {
  private final SlamParticle[] slamParticles;
  private final String logFilename;
  private final boolean saveSlamMap;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;
  // ---
  private MapProvider occurrenceMap;
  /** unitless pose estimated by algorithm */
  private Tensor poseUnitless;
  /** position of most recent event in go kart frame */
  private double[] eventGokartFrame = null;
  // ---
  private Mat labels;

  public SlamCoreContainer(SlamCoreConfig slamConfig) {
    int numOfPart = Magnitude.ONE.toInt(slamConfig.numberOfParticles);
    slamParticles = SlamParticles.allocate(numOfPart);
    saveSlamMap = slamConfig.saveSlamMap;
    logFilename = slamConfig.davisConfig.logFilename();
    linVelAvg = Magnitude.VELOCITY.toDouble(slamConfig.linVelAvg);
    linVelStd = Magnitude.VELOCITY.toDouble(slamConfig.linVelStd);
    angVelStd = Magnitude.PER_SECOND.toDouble(slamConfig.angVelStd);
    occurrenceMap = new MapProvider(slamConfig);
    // ---
    labels = new Mat(slamConfig.mapWidth(), slamConfig.mapHeight(), opencv_core.CV_8U);
  }

  /** @param initPose {x[m], y[m], angle[-]} */
  public void initialize(Tensor initPose) {
    SlamCoreContainerUtil.setInitialDistribution(slamParticles, initPose, linVelAvg, linVelStd, angVelStd);
    setPose(initPose);
  }

  /** saves the occurrence map after log file is completed if required by algorithm configuration */
  public void stop() {
    if (saveSlamMap) {
      PrimitivesIO.saveToCSV( //
          SlamFileLocations.RECORDED_MAP.inFolder(logFilename), //
          occurrenceMap.getMapArray());
      System.out.println("Slam map successfully saved");
    }
  }

  public SlamParticle[] getSlamParticles() {
    return slamParticles;
  }

  public MapProvider getOccurrenceMap() {
    return occurrenceMap;
  }

  /** @param eventGokartFrame null is allowed input */
  public void setEventGokartFrame(double[] eventGokartFrame) {
    this.eventGokartFrame = eventGokartFrame;
  }

  /** @return can be null if event is further away than lookAheadDistance */
  public double[] getEventGokartFrame() {
    return eventGokartFrame;
  }

  public void setPoseUnitless(Tensor unitlessPose) {
    poseUnitless = unitlessPose;
  }

  @Override // from GokartPoseUnitlessInterface
  public Tensor getPoseUnitless() {
    return poseUnitless;
  }

  /** sets pose with when input argument is not unitless
   * 
   * @param pose {x[m], y[m], angle[]} */
  public void setPose(Tensor pose) {
    this.poseUnitless = GokartPoseHelper.toUnitless(pose);
  }

  public void setMat(Mat labels) {
    this.labels = labels;
  }

  public Mat getMat() {
    return labels;
  }
}
