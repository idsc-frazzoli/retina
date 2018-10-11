// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCPathParameter implements MPCNativeInsertable {
  final Scalar X1;

  // TODO: actually implement this
  public MPCPathParameter(ByteBuffer byteBuffer) {
    // dummy constructor
    X1 = Quantity.of(byteBuffer.getFloat(), SI.METER);
  }

  public MPCPathParameter(Scalar X1) {
    // dummy function
    this.X1 = X1;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(Magnitude.METER.toFloat(X1));
  }

  @Override
  public int length() {
    return 4;
  }
}
