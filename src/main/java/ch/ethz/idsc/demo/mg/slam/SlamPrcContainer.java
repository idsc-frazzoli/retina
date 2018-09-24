// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** container for detected way points and curves which are estimated from the points */
public class SlamPrcContainer {
  private final GokartPoseUnitlessInterface slamPose;
  // ---
  /** most recently detected way points */
  private SlamWaypoints slamWaypoints;
  /** fitted curve through valid way points in go kart frame */
  private Tensor fittedCurve;
  /** inter- and extrapolated curve to be passed to pure pursuit controller, world frame */
  private Optional<Tensor> curve;

  public SlamPrcContainer(GokartPoseUnitlessInterface slamPose) {
    this.slamPose = slamPose;
    slamWaypoints = new SlamWaypoints();
    fittedCurve = Tensors.empty();
    curve = Optional.empty();
  }

  /** saves the detected way points in a SlamWaypoints object with all points considered valid
   * 
   * @param worldWaypoints world frame */
  public void setWaypoints(Tensor worldWaypoints) {
    SlamPrcContainerUtil.setSlamWaypoints(worldWaypoints, slamPose.getPoseUnitless(), slamWaypoints);
  }

  /** for visualization */
  public Tensor getWorldWaypoints() {
    return slamWaypoints.getWorldWaypoints();
  }

  public Tensor getGokartWaypoints() {
    return slamWaypoints.getGokartWaypoints();
  }

  public boolean[] getValidities() {
    return slamWaypoints.getValidities();
  }

  public Tensor getValidGokartWaypoints() {
    return slamWaypoints.getValidGokartWaypoints();
  }

  public void setValidities(boolean[] validity) {
    slamWaypoints.setValidities(validity);
  }

  /** @param curve in go kart frame */
  public void setCurve(Tensor curve) {
    Tensor worldCurve = SlamPrcContainerUtil.local2World(curve, slamPose.getPoseUnitless());
    this.curve = Optional.of(worldCurve);
  }

  /** @return inter- and extrapolated curve in go kart frame */
  // synchronized since it is accessed from different threads
  public synchronized Optional<Tensor> getCurve() {
    if (curve.isPresent()) {
      Tensor localCurve = SlamPrcContainerUtil.world2Local(curve.get(), slamPose.getPoseUnitless());
      return Optional.of(localCurve);
    }
    return Optional.empty();
  }

  public void setFittedCurve(Tensor fittedCurve) {
    this.fittedCurve = fittedCurve;
  }

  public Tensor getFittedCurve() {
    return fittedCurve;
  }
}
