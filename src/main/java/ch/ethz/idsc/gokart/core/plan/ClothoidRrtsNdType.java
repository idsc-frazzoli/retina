// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.RrtsNdType;
import ch.ethz.idsc.tensor.Tensor;

// TODO JPH OWL
public enum ClothoidRrtsNdType implements RrtsNdType {
  INSTANCE;
  // ---
  @Override
  public Tensor convert(Tensor tensor) {
    return tensor;
  }

  @Override
  public NdCenterInterface getNdCenterInterface(Tensor tensor) {
    return new ClothoidNdCenter(tensor);
  }
}
