// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;

class ParamContainer {
  public static final ParamContainer INSTANCE = TensorProperties.insert( //
      ResourceData.properties("/properties/ParamContainer.properties"), //
      new ParamContainer());
  // ---
  public String value;
  public Scalar maxTor;
  public Tensor shape;
  public Scalar abc;
  public int nono;
}
