//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCOptimizationParameter implements MPCNativeOutputable {
  public final Scalar speedLimit;
  
  //TODO: actually implement this
  
  public MPCOptimizationParameter(InputStream inputStream) throws Exception {
    //dummy constructor
    DataInputStream dataInputStream = new DataInputStream(inputStream);
    speedLimit = Quantity.of(dataInputStream.readFloat(), SI.VELOCITY);
  }
 
  public MPCOptimizationParameter(Scalar speedLimit) {
    //dummy function
    this.speedLimit = speedLimit;
  }
  
  @Override
  public void output(OutputStream outputStream) throws Exception {
    //dummy function
    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
    dataOutputStream.writeFloat(Magnitude.VELOCITY.toFloat(speedLimit));
  }
}
