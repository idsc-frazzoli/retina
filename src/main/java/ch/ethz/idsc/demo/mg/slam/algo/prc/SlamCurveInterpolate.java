// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;

import ch.ethz.idsc.owl.subdiv.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;

/** methods to interpolate a curve from a list of feature points */
/* package */ enum SlamCurveInterpolate {
  ;
  private static final CurveSubdivision CURVE_SUBDIVISION = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);

  /** interpolates between list of points using BSpline2CurveSubdivision
   * 
   * @param featurePoints in go kart frame */
  public static Tensor refineFeaturePoints(List<double[]> featurePoints) {
    Tensor visibleWaypoints = Tensor.of(featurePoints.stream().map(Tensors::vectorDouble));
    return Nest.of(CURVE_SUBDIVISION::string, visibleWaypoints, 2);
  }
}
