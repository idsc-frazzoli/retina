// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.GokartPoseUnitlessInterface;
import ch.ethz.idsc.demo.mg.slam.SlamContainerUtil;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// class which contains point sets and curves estimated by SLAM algorithm
public class SlamCurveContainer {
  private final GokartPoseUnitlessInterface slamPose;
  /** most recent detected way points */
  private List<SlamWaypoint> slamWaypoints = new ArrayList<>();
  // raw detected feature points in world frame
  private Tensor rawPoints = Tensors.empty();
  // go kart frame
  private Tensor featurePoints = Tensors.empty();
  // go kart frame
  private Tensor interpolated = Tensors.empty();
  // world frame
  private Optional<Tensor> curve = Optional.empty();

  public SlamCurveContainer(GokartPoseUnitlessInterface slamPose) {
    this.slamPose = slamPose;
  }

  public void setWorldWaypoints(Tensor worldWaypoints) {
    rawPoints = worldWaypoints;
  }

  public Tensor getWorldWaypoints() {
    return rawPoints;
  }

  /** @param curve in go kart frame */
  public void setCurve(Tensor curve) {
    Tensor worldCurve = SlamContainerUtil.local2World(curve, slamPose.getPoseUnitless());
    this.curve = Optional.of(worldCurve);
  }

  /** @return refinedWaypointCurve in go kart frame */
  public Optional<Tensor> getCurve() {
    if (curve.isPresent()) {
      Tensor localCurve = SlamContainerUtil.world2Local(curve.get(), slamPose.getPoseUnitless());
      return Optional.of(localCurve);
    }
    return Optional.empty();
  }

  public void setWaypoints(List<SlamWaypoint> waypoints) {
    this.slamWaypoints = waypoints;
  }

  public List<SlamWaypoint> getSlamWaypoints() {
    return slamWaypoints;
  }

  public Tensor getPoseUnitless() {
    return slamPose.getPoseUnitless();
  }

  public void setSelectedPoints(Tensor featurePoints) {
    this.featurePoints = featurePoints;
  }

  public Tensor getSelectedPoints() {
    return featurePoints;
  }

  public void setInterpolation(Tensor refineFeaturePoints) {
    interpolated = refineFeaturePoints;
  }

  public Tensor getInterpolated() {
    return interpolated;
  }
}
