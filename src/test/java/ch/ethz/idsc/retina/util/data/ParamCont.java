// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.owly.demo.util.UserHome;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

class ParamCont {
  public static ParamCont INSTANCE = TensorProperties.insert( //
      TensorProperties.load(UserHome.file("some.properties")), new ParamCont());
  // ---
  public String value;
  public Scalar maxTor;
  public Tensor shape;
  public Scalar abc;
  public int nono;
}
