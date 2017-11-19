// code by jph
package ch.ethz.idsc.retina.util.math;

import java.awt.geom.AffineTransform;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @see AffineTransform */
public class AffineFrame3D {
  private final Tensor tensor;
  private final double m00;
  private final double m10;
  private final double m20;
  private final double m01;
  private final double m11;
  private final double m21;
  private final double m02;
  private final double m12;
  private final double m22;
  private final double m03;
  private final double m13;
  private final double m23;

  /** @param matrix in SE3 with dimensions 4 x 4 */
  public AffineFrame3D(Tensor matrix) {
    this.tensor = matrix.copy();
    m00 = matrix.Get(0, 0).number().doubleValue();
    m10 = matrix.Get(1, 0).number().doubleValue();
    m20 = matrix.Get(2, 0).number().doubleValue();
    m01 = matrix.Get(0, 1).number().doubleValue();
    m11 = matrix.Get(1, 1).number().doubleValue();
    m21 = matrix.Get(2, 1).number().doubleValue();
    m02 = matrix.Get(0, 2).number().doubleValue();
    m12 = matrix.Get(1, 2).number().doubleValue();
    m22 = matrix.Get(2, 2).number().doubleValue();
    m03 = matrix.Get(0, 3).number().doubleValue();
    m13 = matrix.Get(1, 3).number().doubleValue();
    m23 = matrix.Get(2, 3).number().doubleValue();
  }

  public Tensor toPoint3D(Tensor point) {
    double px = point.Get(0).number().doubleValue();
    double py = point.Get(1).number().doubleValue();
    double pz = point.Get(2).number().doubleValue();
    return Tensors.vector( //
        m00 * px + m01 * py + m02 * pz + m03, //
        m10 * px + m11 * py + m12 * pz + m13, //
        m20 * px + m21 * py + m22 * pz + m23 //
    );
  }

  /** @param matrix in SE3 with dimensions 4 x 4
   * @return */
  public AffineFrame3D dot(Tensor matrix) {
    return new AffineFrame3D(tensor.dot(matrix));
  }

  public Tensor tensor_copy() {
    return tensor.copy();
  }
}
