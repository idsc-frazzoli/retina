//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

public interface MPCNativeInsertable {
  void input(ByteBuffer byteBuffer);
  int getLength();
}
