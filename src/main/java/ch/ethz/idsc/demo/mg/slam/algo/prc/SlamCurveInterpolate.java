// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.owl.subdiv.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;

/** methods to interpolate a curve from a list of feature points */
/* package */ enum SlamCurveInterpolate {
  ;
  /** interpolates between list of points using BSpline2CurveSubdivision
   * 
   * @param slamContainer
   * @param featurePoints in go kart frame */
  public static void interpolateFeaturePoints(SlamContainer slamContainer, List<double[]> featurePoints) {
    CurveSubdivision curveSubdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor visibleWaypoints = Tensor.of(featurePoints.stream().map(Tensors::vectorDouble));
    Tensor refinedWaypointCurve = Nest.of(curveSubdivision::string, visibleWaypoints, 3);
    slamContainer.setRefinedWaypointCurve(Optional.of(refinedWaypointCurve));
  }
}
