// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** container for detected way points and curves which are estimated from the points */
public class SlamPrcContainer {
  private final GokartPoseUnitlessInterface slamPose;
  /** most recently detected way points */
  private SlamWaypoints slamWaypoints;
  /** interpolated curve through valid way points in go kart frame */
  private Tensor interpolatedCurve;
  /** inter- and extrapolated curve to be passed to pure pursuit controller, world frame */
  private Optional<Tensor> curve;

  public SlamPrcContainer(GokartPoseUnitlessInterface slamPose) {
    this.slamPose = slamPose;
    slamWaypoints = new SlamWaypoints();
    interpolatedCurve = Tensors.empty();
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
    slamWaypoints.setValidity(validity);
  }

  /** @param curve in go kart frame */
  public void setCurve(Tensor curve) {
    Tensor worldCurve = SlamPrcContainerUtil.local2World(curve, slamPose.getPoseUnitless());
    this.curve = Optional.of(worldCurve);
  }

  /** @return refinedWaypointCurve in go kart frame */
  public Optional<Tensor> getCurve() {
    if (curve.isPresent()) {
      Tensor localCurve = SlamPrcContainerUtil.world2Local(curve.get(), slamPose.getPoseUnitless());
      return Optional.of(localCurve);
    }
    return Optional.empty();
  }

  public void setInterpolatedCurve(Tensor interpolatedCurve) {
    this.interpolatedCurve = interpolatedCurve;
  }

  public Tensor getInterpolatedCurve() {
    return interpolatedCurve;
  }
}
