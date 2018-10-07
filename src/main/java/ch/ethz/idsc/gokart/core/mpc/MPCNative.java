//code by mh
package ch.ethz.idsc.gokart.core.mpc;

public enum MPCNative {
  ;
  public static final int TCP_PORT = 4142;
  public static final int TCP_SERVER_PORT = 4143;
  public static final int INITIALMSGSIZE = 100;
  public static final int PREDICTIONSIZE = 10;
  // First Byte of message: which kind of message are we sending?
  // control update: send state -> get control and prediction
  public static final int REQUEST_CONTROL_UPDATE = 0;
  // path update: send new path parameter
  public static final int PATH_UPDATE = 1;
  // parameter update: send new parameters
  public static final int PARAMETER_UPDATE = 2;
  //executable location and name
  public final static String BINARY = "nativeMPC";
  public final static String RELATIVEPATH = "/src_MATLAB/MPCGokart/ForcesMPCPathFollowing/";
}
