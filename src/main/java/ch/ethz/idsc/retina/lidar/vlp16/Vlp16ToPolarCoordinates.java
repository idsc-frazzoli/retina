// code by gjoel
package ch.ethz.idsc.retina.lidar.vlp16;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** transform lidar coordinates from polar to cartesian, and vice versa
 * not compensating twist and incline
 * 
 * class name inspired by Mathematica::ToPolarCoordinates */
public class Vlp16ToPolarCoordinates implements TensorUnaryOperator {
  private static final ScalarUnaryOperator MOD_TWO_PI = Mod.function(Pi.TWO);
  private final Scalar twist;

  /** @param twist in azimuth direction */
  public Vlp16ToPolarCoordinates(Scalar twist) {
    this.twist = twist;
  }

  /** Hint: if the coordinates x, y, z are provided with a Unit,
   * then the Unit is attached to radius in the return vector
   * 
   * @param {x, y, z}
   * @return {azimuth, elevation, radius} */
  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor xyz) {
    Scalar x = xyz.Get(0);
    Scalar y = xyz.Get(1);
    Scalar z = xyz.Get(2);
    /* according to manual
     * return Tensors.of( //
     * ArcTan.of(y, x), //
     * ArcTan.of(Norm._2.of(Tensors.of(x, y)), z), //
     * Norm._2.of(Tensors.of(x, y, z))); */
    return Tensors.of( //
        MOD_TWO_PI.apply(twist.subtract(ArcTan.of(x, y))), //
        ArcTan.of(Hypot.of(x, y), z), //
        Norm._2.ofVector(xyz));
  }
}
