// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

public enum DubendorfFrame {
  ;
  /** dustproof wall */
  public static final Tensor _20190401 = Se2Matrix.translation(Tensors.vector(0, +120)).dot(Tensors.fromString( //
      "{{36.67799433628459, 35.43620650503479, -1900.5265224432885}, {35.43620650503479, -36.67799433628459, 620.3647376620074}, {0.0, 0.0, 1.0}}"));
  // ---
  // "{{42.72771097503904, 42.122947603812364, -4498.645828532184}, {42.122947603812364, -42.72771097503904, 626.4840362558339}, {0.0, 0.0, 1.0}}");
  /** large */
  public static final Tensor _20190311 = Se2Matrix.translation(Tensors.vector(0, +200)).dot(DiagonalMatrix.of(0.9, 0.9, 1).dot(Tensors.fromString( //
      "{{36.67799433628459, 35.43620650503479, -1900.5265224432885}, {35.43620650503479, -36.67799433628459, 620.3647376620074}, {0.0, 0.0, 1.0}}")));
  // public static final Tensor MODEL2PIXEL = Tensors.fromString("{{50, 0, -1000}, {0, -50, 3000}, {0, 0, 1}}");
  // public static final Tensor MODEL2PIXEL = Tensors.fromString(
  // "{{21.57529078604976, 20.84482735590282, -1091.4861896725226}, {20.84482735590282, -21.57529078604976, 364.92043391882794}, {0.0, 0.0, 1.0}}");
  public static final Tensor _20190309 = Tensors.fromString( //
      "{{42.72771097503904, 42.122947603812364, -4322.645828532184}, {42.122947603812364, -42.72771097503904, 729.4840362558339}, {0.0, 0.0, 1.0}}");
}
