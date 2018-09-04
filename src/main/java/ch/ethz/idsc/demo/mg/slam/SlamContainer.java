// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** container for the objects that are passed between different modules of the SLAM algorithm */
public class SlamContainer implements GokartPoseInterface {
  private final SlamParticle[] slamParticles;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;
  // ---
  private MapProvider occurrenceMap;
  /** unitless pose estimated by algorithm */
  private Tensor poseUnitless;
  /** most recent detected way points */
  private List<SlamWaypoint> slamWaypoints = new ArrayList<>();
  /** way point to be followed by pure pursuit algorithm */
  private Optional<SlamWaypoint> selectedSlamWaypoint = Optional.empty();
  /** position of most recent event in go kart frame */
  private double[] eventGokartFrame = null;

  public SlamContainer(SlamConfig slamConfig) {
    int numOfPart = Magnitude.ONE.toInt(slamConfig.numberOfParticles);
    slamParticles = new SlamParticle[numOfPart];
    for (int index = 0; index < numOfPart; ++index)
      slamParticles[index] = new SlamParticle();
    occurrenceMap = new MapProvider(slamConfig);
    linVelAvg = Magnitude.VELOCITY.toDouble(slamConfig.linVelAvg);
    linVelStd = Magnitude.VELOCITY.toDouble(slamConfig.linVelStd);
    angVelStd = Magnitude.PER_SECOND.toDouble(slamConfig.angVelStd);
  }

  /** @param initPose {x[m], y[m], angle[-]} */
  public void initialize(Tensor initPose) {
    SlamContainerUtil.setInitialDistribution(slamParticles, initPose, linVelAvg, linVelStd, angVelStd);
    setPose(initPose);
  }

  /** for interfacing the pure pursuit controller
   * 
   * @return lookAhead [x,y] in go kart frame coordinates */
  public Optional<Tensor> getLookAhead() {
    Optional<SlamWaypoint> selectedWaypoint = selectedSlamWaypoint;
    if (selectedWaypoint.isPresent()) {
      TensorUnaryOperator world2local = new Se2Bijection(poseUnitless).inverse();
      Tensor lookAhead = world2local.apply(Tensors.vectorDouble(selectedWaypoint.get().getWorldPosition()));
      return Optional.of(lookAhead);
    }
    return Optional.empty();
  }

  /** @return list of SlamWaypoints that are visible */
  public List<SlamWaypoint> getVisibleWaypoints() {
    List<SlamWaypoint> visibleWaypoints = new ArrayList<>();
    for (int i = 0; i < slamWaypoints.size(); i++) {
      if (slamWaypoints.get(i).isVisible())
        visibleWaypoints.add(slamWaypoints.get(i));
    }
    return visibleWaypoints;
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

  public void setSelectedSlamWaypoint(Optional<SlamWaypoint> selectedSlamWaypoint) {
    this.selectedSlamWaypoint = selectedSlamWaypoint;
  }

  public Optional<SlamWaypoint> getSelectedSlamWaypoint() {
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
