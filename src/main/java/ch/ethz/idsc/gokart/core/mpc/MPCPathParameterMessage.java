//code by mh
package ch.ethz.idsc.gokart.core.mpc;

/* package */ class MPCPathParameterMessage {
  public final int messageType = MPCNative.PATH_UPDATE;
  public final MPCPathParameter mpcPathParameters;

  public MPCPathParameterMessage(MPCPathParameter mpcPathParameters) {
    this.mpcPathParameters = mpcPathParameters;
  }
}
