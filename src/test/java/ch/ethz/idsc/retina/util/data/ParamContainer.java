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
  public String string;
  public Scalar maxTor;
  public Tensor shape;
  public Scalar abc;
  public Boolean status;
  // ---
  // ignore the following
  public transient Scalar ignoreTransient;
  /* package */ Scalar ignorePackage; // ignored
  public int nono; // int's are ignored
}
