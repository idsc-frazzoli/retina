// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum DubendorfTrack {
  ;
  private static final Scalar RADIUS_DEFAULT = Quantity.of(2.0, SI.METER);
  // ---
  public static final MPCBSplineTrack HYPERLOOP_EIGHT = hyperloop_eight();
  public static final MPCBSplineTrack HYPERLOOP_EIGHT_REVERSE = hyperloop_eight_reverse();
  public static final MPCBSplineTrack CHICANE = chicane_track();
  public static final MPCBSplineTrack WAYPOINT_TRACK = waypoint_track();

  static Tensor getConstantRadius(Scalar radius, int length) {
    return Tensors.vector(i -> radius, length);
  }

  private static MPCBSplineTrack hyperloop_eight() {
    Tensor controlPoints = ResourceData.of("/dubilab/controlpoints/eight/20180603.csv").multiply(Quantity.of(1, SI.METER));
    VectorQ.requireLength(controlPoints.get(0), 2);
    return new MPCBSplineTrack(Tensor.of(controlPoints.stream().map(row -> row.append(RADIUS_DEFAULT))), true);
  }

  private static MPCBSplineTrack hyperloop_eight_reverse() {
    Tensor controlPoints = Reverse.of(ResourceData.of("/dubilab/controlpoints/eight/20180603.csv")).multiply(Quantity.of(1, SI.METER));
    VectorQ.requireLength(controlPoints.get(0), 2);
    return new MPCBSplineTrack(Tensor.of(controlPoints.stream().map(row -> row.append(RADIUS_DEFAULT))), true);
  }

  private static MPCBSplineTrack chicane_track() {
    Tensor controlPointsX = Tensors.empty();
    Tensor controlPointsY = Tensors.empty();
    Tensor controlPointsR = Tensors.empty();
    // add them in code
    /* 36.2,44.933
     * 49.867,58.2
     * 57.2,53.8
     * 53,48
     * 47,47
     * 47,43
     * 41.8,38.333 */
    // X
    // QuantityTensor.of(Tensors.vector(36.2,44.933,2), SI.METER);
    controlPointsX.append(Quantity.of(36.2, SI.METER));
    controlPointsX.append(Quantity.of(52, SI.METER));
    controlPointsX.append(Quantity.of(57.2, SI.METER));
    controlPointsX.append(Quantity.of(53, SI.METER));
    controlPointsX.append(Quantity.of(52, SI.METER));
    controlPointsX.append(Quantity.of(47, SI.METER));
    controlPointsX.append(Quantity.of(41.8, SI.METER));
    // Y
    controlPointsY.append(Quantity.of(44.933, SI.METER));
    controlPointsY.append(Quantity.of(58.2, SI.METER));
    controlPointsY.append(Quantity.of(53.8, SI.METER));
    controlPointsY.append(Quantity.of(49, SI.METER));
    controlPointsY.append(Quantity.of(44, SI.METER));
    controlPointsY.append(Quantity.of(43, SI.METER));
    controlPointsY.append(Quantity.of(38.333, SI.METER));
    // R
    controlPointsR.append(Quantity.of(1.8, SI.METER));
    controlPointsR.append(Quantity.of(1.8, SI.METER));
    controlPointsR.append(Quantity.of(1.8, SI.METER));
    controlPointsR.append(Quantity.of(0.8, SI.METER));
    controlPointsR.append(Quantity.of(0.8, SI.METER));
    controlPointsR.append(Quantity.of(0.8, SI.METER));
    controlPointsR.append(Quantity.of(1.8, SI.METER));
    return new MPCBSplineTrack(Transpose.of(Tensors.of(controlPointsX, controlPointsY, controlPointsR)), true);
  }

  private static MPCBSplineTrack waypoint_track() {
    Tensor controlPointsX = Tensors.empty();
    Tensor controlPointsY = Tensors.empty();
    // add them in code
    /* 36.82,44.18
     * 44.03,51.39
     * 51.15,55.33
     * 54.17,49.67
     * 47.99,42.63
     * 40.94,36.45
     * 35.45,41.94 */
    // X
    controlPointsX.append(Quantity.of(36.82, SI.METER));
    controlPointsX.append(Quantity.of(44.03, SI.METER));
    controlPointsX.append(Quantity.of(51.15, SI.METER));
    controlPointsX.append(Quantity.of(54.17, SI.METER));
    controlPointsX.append(Quantity.of(47.99, SI.METER));
    controlPointsX.append(Quantity.of(40.94, SI.METER));
    controlPointsX.append(Quantity.of(35.45, SI.METER));
    // Y
    controlPointsY.append(Quantity.of(44.18, SI.METER));
    controlPointsY.append(Quantity.of(51.39, SI.METER));
    controlPointsY.append(Quantity.of(55.33, SI.METER));
    controlPointsY.append(Quantity.of(49.67, SI.METER));
    controlPointsY.append(Quantity.of(42.63, SI.METER));
    controlPointsY.append(Quantity.of(36.45, SI.METER));
    controlPointsY.append(Quantity.of(41.94, SI.METER));
    return new MPCBSplineTrack(Transpose.of( //
        Tensors.of(controlPointsX, controlPointsY, getConstantRadius(RADIUS_DEFAULT, controlPointsX.length()))), true);
  }
}
