// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

public enum RieterFrame {
  ;
  /** total area */
  public static final Tensor _20191022 = Se2Matrix.translation(Tensors.vector(0, -500)).dot(DiagonalMatrix.of(1.5, 1.5, 1).dot(Tensors.fromString( //
      "{{14.999747616820807, -0.08701397404036998, 20.08415508531209}, {-0.08701397404036998, -14.999747616820807, 1244.2788562597827}, {0.0, 0.0, 1.0}}")));
  /** upper area only */
  public static final Tensor UPPER = Se2Matrix.translation(Tensors.vector(-1200, -1350)).dot(DiagonalMatrix.of(4, 4, 1).dot(Tensors.fromString( //
      "{{14.999747616820807, -0.08701397404036998, 20.08415508531209}, {-0.08701397404036998, -14.999747616820807, 1244.2788562597827}, {0.0, 0.0, 1.0}}")));
  /** lower area only */
  public static final Tensor LOWER = Se2Matrix.translation(Tensors.vector(-1400, -2550)).dot(DiagonalMatrix.of(4, 4, 1).dot(Tensors.fromString( //
      "{{14.999747616820807, -0.08701397404036998, 20.08415508531209}, {-0.08701397404036998, -14.999747616820807, 1244.2788562597827}, {0.0, 0.0, 1.0}}")));
  /** lower area full width */
  public static final Tensor LOWER_WIDE = Se2Matrix.translation(Tensors.vector(-700, -1350)).dot(DiagonalMatrix.of(2.5, 2.5, 1).dot(Tensors.fromString( //
      "{{14.999747616820807, -0.08701397404036998, 20.08415508531209}, {-0.08701397404036998, -14.999747616820807, 1244.2788562597827}, {0.0, 0.0, 1.0}}")));
}
