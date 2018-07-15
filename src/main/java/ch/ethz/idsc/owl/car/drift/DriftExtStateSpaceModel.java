// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.sca.Cos;

class DriftExtStateSpaceModel implements StateSpaceModel, Serializable {
  private final DriftStateSpaceModel driftStateSpaceModel;

  public DriftExtStateSpaceModel(DriftParameters driftParameters) {
    driftStateSpaceModel = new DriftStateSpaceModel(driftParameters);
  }

  @Override
  public Tensor f(Tensor x, Tensor u) {
    Tensor dxLower = driftStateSpaceModel.f(x.extract(3, 6), u);
    Scalar beta = x.Get(3);
    Scalar r = x.Get(4);
    Scalar Ux = x.Get(5);
    Scalar theta = x.Get(2);
    Scalar U = Ux.divide(Cos.of(beta));
    // Scalar UxWorld = U.multiply(Cos.of(beta.add(theta)));
    // Scalar UyWorld = U.multiply(Sin.of(beta.add(theta)));
    return Join.of(AngleVector.of(beta.add(theta)).multiply(U), Tensors.of(r), dxLower);
  }

  @Override
  public Scalar getLipschitz() {
    return null;
  }
}
