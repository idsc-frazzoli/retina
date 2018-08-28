// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** container for the objects that are modified by the SLAM algorithm */
// TODO MG initialization for localization mode
public class SlamContainer implements GokartPoseInterface {
  private final SlamParticle[] slamParticles;
  private final MapProvider occurrenceMap;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;
  // ---
  /** unitless pose estimated by algorithm */
  private Tensor poseUnitless;
  /** most recent detected waypoints */
  private List<SlamWaypoint> waypoints = new ArrayList<>();
  /** way point to be followed by pure pursuit algorithm */
  private SlamWaypoint selectedSlamWaypoint;
  /** position of most recent event in go kart frame */
  private double[] eventGokartFrame;

  public SlamContainer(SlamConfig slamConfig) {
    int numOfPart = slamConfig.numberOfParticles.number().intValue();
    slamParticles = new SlamParticle[numOfPart];
    for (int index = 0; index < numOfPart; ++index)
      slamParticles[index] = new SlamParticle();
    occurrenceMap = new MapProvider(slamConfig);
    linVelAvg = Magnitude.VELOCITY.toDouble(slamConfig.linVelAvg);
    linVelStd = Magnitude.VELOCITY.toDouble(slamConfig.linVelStd);
    angVelStd = Magnitude.PER_SECOND.toDouble(slamConfig.angVelStd);
    eventGokartFrame = null;
  }

  /** @param initPose {x[m], y[m], angle[-]} */
  public void initialize(Tensor initPose) {
    SlamContainerUtil.setInitialDistribution(slamParticles, initPose, linVelAvg, linVelStd, angVelStd);
    setPose(initPose);
  }

  public SlamParticle[] getSlamParticles() {
    return slamParticles;
  }

  public MapProvider getOccurrenceMap() {
    return occurrenceMap;
  }

  public void setWaypoints(List<SlamWaypoint> waypoints) {
    this.waypoints = waypoints;
  }

  public List<SlamWaypoint> getSlamWaypoints() {
    return waypoints;
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

  public void setSelectedSlamWaypoint(SlamWaypoint selectedSlamWaypoint) {
    this.selectedSlamWaypoint = selectedSlamWaypoint;
  }

  public SlamWaypoint getSelectedSlamWaypoint() {
    return selectedSlamWaypoint;
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
