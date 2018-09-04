// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;

class ParamContainer {
  public static final ParamContainer INSTANCE = TensorProperties.wrap(new ParamContainer()) //
      .set(ResourceData.properties("/properties/ParamContainer.properties"));
  // ---
  public String string;
  public Scalar maxTor;
  public Tensor shape;
  public Scalar abc;
  public Boolean status;
  // ---
  // ignore the following
  public transient Scalar _transient;
  /* package */ Scalar _package;
  public int nono; // int's are ignored
  public final Scalar _final = RealScalar.ONE;
  protected Boolean _protected;
  public static String _static = "string value";
}
