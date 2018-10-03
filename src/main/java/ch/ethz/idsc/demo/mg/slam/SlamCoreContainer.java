// code by mg
package ch.ethz.idsc.demo.mg.slam;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.retina.util.io.PrimitivesIO;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** container for the objects that are passed between different modules of
 * the core part of the SLAM algorithm */
public class SlamCoreContainer implements GokartPoseUnitlessInterface {
  private final SlamParticle[] slamParticles;
  private final String logFilename;
  private final boolean saveSlamMap;
  // ---
  private MapProvider occurrenceMap;
  /** unitless pose estimated by algorithm */
  private Tensor poseUnitless;
  /** position of most recent event in go kart frame */
  private double[] eventGokartFrame = null;
  // ---
  private Mat labels;

  public SlamCoreContainer() {
    int numOfPart = Magnitude.ONE.toInt(SlamCoreConfig.GLOBAL.numberOfParticles);
    slamParticles = SlamParticles.allocate(numOfPart);
    saveSlamMap = SlamCoreConfig.GLOBAL.saveSlamMap;
    logFilename = SlamCoreConfig.GLOBAL.davisConfig.logFilename();
    occurrenceMap = new MapProvider(SlamCoreConfig.GLOBAL);
    // ---
    labels = new Mat(SlamCoreConfig.GLOBAL.mapWidth(), SlamCoreConfig.GLOBAL.mapHeight(), opencv_core.CV_8U);
  }

  /** @param initPose {x[m], y[m], angle[-]} */
  public void initialize(Tensor initPose) {
    SlamCoreContainerUtil.setInitialDistribution(slamParticles, initPose);
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

  public void setPoseUnitless(Tensor poseUnitless) {
    this.poseUnitless = poseUnitless;
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

  public void setLabels(Mat labels) {
    this.labels = labels;
  }

  public Mat getLabels() {
    return labels;
  }
}
