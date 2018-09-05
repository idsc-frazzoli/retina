// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.util.io.PrimitivesIO;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** container for the objects that are passed between different modules of the SLAM algorithm */
public class SlamContainer implements GokartPoseInterface {
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
  /** most recent detected way points */
  private List<SlamWaypoint> slamWaypoints = new ArrayList<>();
  /** lookahead to be followed by pure pursuit algorithm */
  private Optional<double[]> lookAheadWorldFrame = Optional.empty();
  /** position of most recent event in go kart frame */
  private double[] eventGokartFrame = null;

  public SlamContainer(SlamConfig slamConfig) {
    int numOfPart = Magnitude.ONE.toInt(slamConfig.numberOfParticles);
    slamParticles = new SlamParticle[numOfPart];
    for (int index = 0; index < numOfPart; ++index)
      slamParticles[index] = new SlamParticle();
    saveSlamMap = slamConfig.saveSlamMap;
    logFilename = slamConfig.davisConfig.logFilename();
    linVelAvg = Magnitude.VELOCITY.toDouble(slamConfig.linVelAvg);
    linVelStd = Magnitude.VELOCITY.toDouble(slamConfig.linVelStd);
    angVelStd = Magnitude.PER_SECOND.toDouble(slamConfig.angVelStd);
    occurrenceMap = new MapProvider(slamConfig);
  }

  /** @param initPose {x[m], y[m], angle[-]} */
  public void initialize(Tensor initPose) {
    SlamContainerUtil.setInitialDistribution(slamParticles, initPose, linVelAvg, linVelStd, angVelStd);
    setPose(initPose);
  }

  /** for interfacing the pure pursuit controller
   * 
   * @return lookAhead {x,y} in go kart frame coordinates */
  public Optional<Tensor> getLookAhead() {
    Optional<double[]> lookAheadWorldFrame = this.lookAheadWorldFrame;
    if (lookAheadWorldFrame.isPresent()) {
      TensorUnaryOperator world2local = new Se2Bijection(poseUnitless).inverse();
      Tensor lookAhead = world2local.apply(Tensors.vectorDouble(lookAheadWorldFrame.get()));
      return Optional.of(lookAhead);
    }
    return Optional.empty();
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

  public void setWaypoints(List<SlamWaypoint> waypoints) {
    this.slamWaypoints = waypoints;
  }

  public List<SlamWaypoint> getSlamWaypoints() {
    return slamWaypoints;
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

  public Tensor getPoseUnitless() {
    return poseUnitless;
  }

  /** @param lookAheadWorldFrame in world frame coordinates */
  public void setLookAhead(Optional<double[]> lookAheadWorldFrame) {
    this.lookAheadWorldFrame = lookAheadWorldFrame;
  }

  /** for visualization */
  public Optional<double[]> getlookAheadWorldFrame() {
    return lookAheadWorldFrame;
  }

  /** sets pose with when input argument is not unitless
   * 
   * @param pose {x[m], y[m], angle[]} */
  public void setPose(Tensor pose) {
    this.poseUnitless = GokartPoseHelper.toUnitless(pose);
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return GokartPoseHelper.attachUnits(poseUnitless);
  }
}
