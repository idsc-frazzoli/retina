//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class GokartStateMessage implements MPCNativeOutputable {
  public final GokartState gokartState;
  public final Date creationTime;

  public GokartStateMessage(GokartState gokartState) {
    creationTime = new Date();
    this.gokartState = gokartState;
  }

  public GokartStateMessage(InputStream inputStream) throws Exception {
    // This is only for testing purposes
    // first int = state message tag
    DataInputStream dataInputStream = new DataInputStream(inputStream);
    if (dataInputStream.readInt() != MPCNative.GOKART_STATE)
      throw new IllegalArgumentException("Not a Gokart State message");
    creationTime = new Date(dataInputStream.readLong());
    gokartState = new GokartState(inputStream);
  }

  @Override
  public void output(OutputStream outputStream) throws Exception {
    // write message
    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
    dataOutputStream.writeInt(MPCNative.GOKART_STATE);
    dataOutputStream.writeLong(creationTime.getTime());
    gokartState.output(outputStream);
  }
}
