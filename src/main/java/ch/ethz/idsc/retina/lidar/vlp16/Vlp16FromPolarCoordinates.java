// code by gjoel
package ch.ethz.idsc.retina.lidar.vlp16;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** transform lidar coordinates from polar to cartesian, and vice versa
 * not compensating twist and incline
 * 
 * class name inspired by Mathematica::FromPolarCoordinates */
public class Vlp16FromPolarCoordinates implements TensorUnaryOperator {
  private final Scalar twist;

  /** @param twist in azimuth direction */
  public Vlp16FromPolarCoordinates(Scalar twist) {
    this.twist = twist;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor vector) {
    return of(vector.Get(0), vector.Get(1), vector.Get(2));
  }

  /** Hint: if the radius is provided with a Unit,
   * then the Unit is attached to entries of the return vector
   * 
   * @param azimuth in [rad]
   * @param elevation in [rad]
   * @param radius
   * @return {x, y, z} */
  private Tensor of(Scalar azimuth, Scalar elevation, Scalar radius) {
    azimuth = azimuth.negate().add(twist);
    /* according to manual
     * return Tensors.of( //
     * radius.multiply(Cos.of(elevation)).multiply(Sin.of(azimuth)), //
     * radius.multiply(Cos.of(elevation)).multiply(Cos.of(azimuth)), //
     * radius.multiply(Sin.of(elevation))); */
    Scalar rad_cos_elev = radius.multiply(Cos.of(elevation));
    return Tensors.of( //
        rad_cos_elev.multiply(Cos.of(azimuth)), //
        rad_cos_elev.multiply(Sin.of(azimuth)), //
        radius.multiply(Sin.of(elevation)));
  }
}
