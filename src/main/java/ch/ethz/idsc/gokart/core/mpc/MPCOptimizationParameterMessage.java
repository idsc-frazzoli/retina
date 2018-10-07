//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/* package */  class MPCOptimizationParameterMessage implements MPCNativeOutputable {
  public final int messageType = MPCNative.PARAMETER_UPDATE;
  public final MPCOptimizationParameter mpcOptimizationParameter;
  public final Date creationTime;


  public MPCOptimizationParameterMessage(MPCOptimizationParameter mpcOptimizationParameter) {
    this.creationTime = new Date();
    this.mpcOptimizationParameter = mpcOptimizationParameter;
  }

  public MPCOptimizationParameterMessage(InputStream inputStream) throws Exception {
    DataInputStream dataInputStream = new DataInputStream(inputStream);
    if (dataInputStream.readInt() != MPCNative.PARAMETER_UPDATE)
      throw new IllegalArgumentException("Not a Optimization Parameter message");
    creationTime = new Date(dataInputStream.readLong());
    mpcOptimizationParameter = new MPCOptimizationParameter(inputStream);
  }

  @Override
  public void output(OutputStream outputStream) throws Exception {
    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
    dataOutputStream.writeInt(MPCNative.PATH_UPDATE);
    dataOutputStream.writeLong(creationTime.getTime());
    mpcOptimizationParameter.output(outputStream);
  }
}
