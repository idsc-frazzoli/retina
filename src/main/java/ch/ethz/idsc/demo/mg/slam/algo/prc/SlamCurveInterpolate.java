// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.owl.subdiv.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;

/** methods to interpolate a curve from a list of visible way points */
/* package */ enum SlamCurveInterpolate {
  ;
  /** interpolates between the visible way points using BSpline2CurveSubdivision
   * 
   * @param slamContainer
   * @param visibleWaypointsList in go kart coordinates */
  public static void interpolateWaypoints(SlamContainer slamContainer, List<double[]> visibleWaypointsList) {
    CurveSubdivision curveSubdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor visibleWaypoints = Tensor.of(visibleWaypointsList.stream().map(Tensors::vectorDouble));
    Tensor refinedWaypointCurve = Nest.of(curveSubdivision::string, visibleWaypoints, 4);
    slamContainer.setRefinedWaypointCurve(refinedWaypointCurve);
  }
}
