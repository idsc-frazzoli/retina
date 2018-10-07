//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.OutputStream;

public interface MPCNativeOutputable {
  public void output(OutputStream outputStream) throws Exception;
}
