// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.io.File;

import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.curve.FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Nest;

public enum DubendorfCurve {
  ;
  private static final TensorUnaryOperator SUBDIVISION_RN = //
      new FourPointCurveSubdivision(RnGeodesic.INSTANCE)::cyclic;
  private static final TensorUnaryOperator SUBDIVISION_SE2 = //
      new FourPointCurveSubdivision(Se2Geodesic.INSTANCE)::cyclic;
  // ---
  /** CURVE "OVAL" IS USED IN TESTS
   * DONT MODIFY COORDINATES - INSTEAD CREATE A NEW CURVE */
  public static final Tensor OVAL = oval();
  public static final Tensor REVERSE_OVAL = Reverse.of(oval());
  // ---
  public static final Tensor TRACK_OVAL = track_oval();
  public static final Tensor TRACK_OVAL_SE2 = track_oval_se2();

  /** CURVE "OVAL" IS USED IN TESTS
   * DONT MODIFY COORDINATES - INSTEAD CREATE A NEW CURVE */
  private static Tensor oval() {
    Tensor poly = Tensors.of( //
        Tensors.vector(35.200, 44.933), //
        Tensors.vector(49.867, 59.200), //
        Tensors.vector(57.200, 54.800), //
        Tensors.vector(49.200, 45.067), //
        Tensors.vector(40.800, 37.333));
    poly = poly.map(scalar -> Quantity.of(scalar, SI.METER));
    return Nest.of(SUBDIVISION_RN, poly, 6).unmodifiable();
  }

  private static Tensor project_se2_r2(Tensor control) {
    return Tensor.of(control.stream().map(Extract2D.FUNCTION));
  }

  private static Tensor track_oval() {
    Tensor poly = track_oval_se2();
    return Tensors.isEmpty(poly) //
        ? Tensors.empty()
        : project_se2_r2(poly).unmodifiable();
  }

  private static Tensor track_oval_se2() {
    try {
      Tensor poly = Import.of(new File("resources/track20190325.csv"));
      return Nest.of(SUBDIVISION_SE2, poly, 4).unmodifiable();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return Tensors.empty();
  }
}
