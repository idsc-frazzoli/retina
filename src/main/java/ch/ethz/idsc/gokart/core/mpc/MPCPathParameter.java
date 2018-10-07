//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCPathParameter implements MPCNativeOutputable {
  final Scalar X1;

  // TODO: actually implement this
  public MPCPathParameter(InputStream inputStream) throws Exception {
    // dummy constructor
    DataInputStream dataInputStream = new DataInputStream(inputStream);
    X1 = Quantity.of(dataInputStream.readFloat(), SI.METER);
  }

  public MPCPathParameter(Scalar X1) {
    // dummy function
    this.X1 = X1;
  }

  @Override
  public void output(OutputStream outputStream) throws Exception {
    // dummy function
    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
    dataOutputStream.writeFloat(Magnitude.METER.toFloat(X1));
  }
}
