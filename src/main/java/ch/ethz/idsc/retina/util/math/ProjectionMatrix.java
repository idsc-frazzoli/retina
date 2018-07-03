// code by jph
// https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/gluPerspective.xml
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Cot;
import ch.ethz.idsc.tensor.sca.Sign;

public enum ProjectionMatrix {
  ;
  /** @param fovy
   * @param aspect
   * @param zNear
   * @param zFar
   * @return */
  public static Tensor of(double fovy, double aspect, double zNear, double zFar) {
    return of(RealScalar.of(fovy), RealScalar.of(aspect), Clip.function(zNear, zFar));
  }

  /** zNear positive distance from the viewer to the near clipping plane
   * zFar positive distance from the viewer to the far clipping plane greater than zNear
   * 
   * @param fovy field of view angle in the y direction
   * @param aspect ratio of x (width) to y (height)
   * @param clip from 0 < zNear < zFar
   * @return matrix with dimensions 4 x 4 */
  public static Tensor of(Scalar fovy, Scalar aspect, Clip clip) {
    Scalar zNear = Sign.requirePositive(clip.min());
    Scalar zFar = clip.max();
    Scalar f = Cot.of(fovy.multiply(RationalScalar.of(1, 2)));
    Tensor matrix = Array.zeros(4, 4);
    matrix.set(f.divide(aspect), 0, 0);
    matrix.set(f, 1, 1);
    matrix.set(zNear.add(zFar).divide(zNear.subtract(zFar)), 2, 2);
    matrix.set(zNear.multiply(zFar).multiply(RealScalar.of(2)).divide(zNear.subtract(zFar)), 2, 3);
    matrix.set(RealScalar.ONE.negate(), 3, 2);
    return matrix;
  }
}
