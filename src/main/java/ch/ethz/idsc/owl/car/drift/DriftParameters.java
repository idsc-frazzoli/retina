// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.io.Serializable;

import ch.ethz.idsc.owl.car.math.Pacejka3;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** THIS CLASS IS USED IN TESTS !!!
 * DO NOT CHANGE THE VALUES IN THIS CLASS
 * PLEASE, MODIFY A COPY INSTEAD */
public class DriftParameters implements Serializable {
  private static final Scalar GRAVITATION = RealScalar.of(9.81);
  // ---
  /** mass [kg] */
  public final Scalar m = RealScalar.of(1412);
  /** yawing moment of inertia [kgm2] */
  private final Scalar Iz = RealScalar.of(1536.7 + 427.7084); // sprung mass inertia + unsprung mass inertia
  private final Scalar Iz_reciprocal = Iz.reciprocal(); // sprung mass inertia + unsprung mass inertia
  /** front axle distance from COG [m] */
  public final Scalar a = RealScalar.of(1.015);
  /** rear axle distanc from COG [m] */
  public final Scalar b = RealScalar.of(1.895);
  /** pacejka model parameters
   * 1 - for frint tires, 2 - rear tires
   * Pacejka3
   * Scalar B1 = 13.8509;par.C1=1.367;par.D1=0.9622;
   * Scalar B2 = 14.1663;par.C2=1.3652;par.D2=0.9744;
   * choose average */
  public final Pacejka3 pacejka3 = new Pacejka3( //
      (13.8509 + 14.1663) / 2, //
      (1.367 + 1.3652) / 2, //
      (0.9622 + 0.9744) / 2);
  /** friction */
  public final Scalar muF = RealScalar.of(0.55);
  public final Scalar muR = RealScalar.of(0.53);
  /** constant expressions that used to be functions */
  private final Scalar mg = GRAVITATION.multiply(m);
  public final Scalar Fz_F = mg.multiply(b).divide(a.add(b));
  public final Scalar Fz_R = mg.multiply(a).divide(a.add(b));

  public Scalar Iz_invert() {
    return Iz_reciprocal;
  }
}
