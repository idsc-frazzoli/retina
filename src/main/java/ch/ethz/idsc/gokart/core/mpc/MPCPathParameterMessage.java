//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/* package */ class MPCPathParameterMessage implements MPCNativeOutputable {
  public final int messageType = MPCNative.PATH_UPDATE;
  public final MPCPathParameter mpcPathParameters;
  public final Date creationTime;

  public MPCPathParameterMessage(MPCPathParameter mpcPathParameters) {
    this.creationTime = new Date();
    this.mpcPathParameters = mpcPathParameters;
  }

  public MPCPathParameterMessage(InputStream inputStream) throws Exception {
    DataInputStream dataInputStream = new DataInputStream(inputStream);
    if (dataInputStream.readInt() != MPCNative.PATH_UPDATE)
      throw new IllegalArgumentException("Not a Path Parameter message");
    creationTime = new Date(dataInputStream.readLong());
    mpcPathParameters = new MPCPathParameter(inputStream);
  }

  @Override
  public void output(OutputStream outputStream) throws Exception {
    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
    dataOutputStream.writeInt(MPCNative.PATH_UPDATE);
    dataOutputStream.writeLong(creationTime.getTime());
    mpcPathParameters.output(outputStream);
  }
}
