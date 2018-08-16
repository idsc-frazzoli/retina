// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sin;

/** The Pacejka "Magic Formula" tire models
 * 
 * Pacejka3 depends on 3 parameters: B, C, D
 * 
 * https://en.wikipedia.org/wiki/Hans_B._Pacejka
 * 
 * Examples:
 * Time-Optimal Vehicle Posture Control to Mitigate Unavoidable
 * Collisions Using Conventional Control Inputs
 * B=7, C=1.4, D=1
 * 
 * Important: {@link Pacejka3} is not continuous for several input
 * Tensors.vector(0, 1);
 * Tensors.vector(0, 0); */
public class Pacejka3 implements ScalarUnaryOperator {
  private static final Scalar PI_HALF = RealScalar.of(Math.PI / 2);
  // ---
  private final Scalar B;
  private final Scalar C;
  private final Scalar D;

  /** @param B
   * @param C
   * @param D */
  public Pacejka3(double B, double C, double D) {
    this.B = RealScalar.of(B);
    this.C = RealScalar.of(C);
    this.D = RealScalar.of(D);
  }

  /** @param B
   * @param C */
  public Pacejka3(double B, double C) {
    this(B, C, 1);
  }

  @Override
  public Scalar apply(Scalar slip) {
    if (!NumberQ.of(slip))
      throw TensorRuntimeException.of(slip);
    return D.multiply(Sin.of(C.multiply(ArcTan.of(B.multiply(slip)))));
  }

  /** the limit case has been established with Mathematica.
   * for reasonable pacejka constants, the "total" variable will evaluate to be positive.
   * due to the negate the final friction "mu" is then pointing against the ground speed {ux, uy} */
  public Scalar limit() {
    return D.multiply(Sin.of(C.multiply(PI_HALF)));
  }
}
