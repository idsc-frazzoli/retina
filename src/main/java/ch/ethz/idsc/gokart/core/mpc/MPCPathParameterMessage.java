//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.Serializable;

public class MPCPathParameterMessage implements Serializable {
  private static final long serialVersionUID = 1L;
  public final int messageType = MPCNative.PATH_UPDATE;
  public final MPCPathParameters mpcPathParameters;
  public MPCPathParameterMessage(MPCPathParameters mpcPathParameters) {
    this.mpcPathParameters = mpcPathParameters;
  }
}
