// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Sqrt;

class DriftStateSpaceModel implements StateSpaceModel, Serializable {
  private final DriftParameters driftParameters;

  public DriftStateSpaceModel(DriftParameters driftParameters) {
    this.driftParameters = driftParameters;
  }

  @Override
  public Tensor f(Tensor x, Tensor u) {
    Scalar beta = x.Get(0);
    Scalar r = x.Get(1);
    Scalar Ux = x.Get(2);
    Scalar delta = u.Get(0);
    Scalar FxR = u.Get(1);
    Scalar FyF = Fy_F(beta, r, Ux, delta);
    Scalar FyR = Fy_R(beta, r, Ux, FxR);
    Scalar dbeta = FyF.add(FyR).divide(driftParameters.m.multiply(Ux)).subtract(r);
    Scalar dr = driftParameters.a.multiply(FyF).subtract(driftParameters.b.multiply(FyR)).multiply(driftParameters.Iz_invert());
    Scalar dUx = FxR.subtract(FyF.multiply(Sin.of(delta))).divide(driftParameters.m).add(r.multiply(Ux.multiply(beta)));
    return Tensors.of(dbeta, dr, dUx);
  }

  private Scalar Fy_F(Scalar beta, Scalar r, Scalar Ux, Scalar delta) {
    Scalar aF = a_F(beta, r, Ux, delta);
    Scalar FzF = driftParameters.Fz_F;
    return pacejka(aF, RealScalar.ZERO, FzF, driftParameters.muF);
  }

  private Scalar Fy_R(Scalar beta, Scalar r, Scalar Ux, Scalar FxR) {
    Scalar aR = a_R(beta, r, Ux);
    Scalar FzR = driftParameters.Fz_R;
    return pacejka(aR, FxR, FzR, driftParameters.muR);
  }

  private Scalar a_F(Scalar beta, Scalar r, Scalar Ux, Scalar delta) {
    // return ArcTan.of(beta.add(driftParameters.a.multiply(r).divide(Ux))).subtract(delta);
    return ArcTan.of(Ux, beta.multiply(Ux).add(driftParameters.a.multiply(r))).subtract(delta);
  }

  private Scalar a_R(Scalar beta, Scalar r, Scalar Ux) {
    // return ArcTan.of(beta.subtract(driftParameters.b.multiply(r).divide(Ux)));
    return ArcTan.of(Ux, beta.multiply(Ux).subtract(driftParameters.b.multiply(r)));
  }

  private Scalar pacejka(Scalar slip, Scalar Fx, Scalar Fz, Scalar mu) {
    final Scalar muFz = mu.multiply(Fz);
    if (Scalars.lessThan(muFz, Fx))
      return RealScalar.ZERO;
    // Scalar eps = Sqrt.of(muFz.multiply(muFz).subtract(Fx.multiply(Fx))).divide(muFz);
    // return eps.multiply(muFz).multiply(driftParameters.pacejka3.apply(slip)).negate();
    Scalar eps = Sqrt.of(muFz.multiply(muFz).subtract(Fx.multiply(Fx)));
    return eps.multiply(driftParameters.pacejka3.apply(slip)).negate();
  }

  @Override
  public Scalar getLipschitz() {
    return null;
  }
}
