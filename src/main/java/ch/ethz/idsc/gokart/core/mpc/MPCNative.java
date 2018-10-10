// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.File;
import java.util.Optional;

/* package */ enum MPCNative {
  ;
  public static final int TCP_PORT = 4142;
  public static final int TCP_SERVER_PORT = 4143;
  public static final int INITIALMSGSIZE = 100;
  public static final int PREDICTIONSIZE = 10;

  public static final int GOKART_STATE = 0;
  /** First Byte of message: which kind of message are we sending?
   * control update: send state -> get control and prediction */
  public static final int REQUEST_CONTROL_UPDATE = 0;
  /** path update: send new path parameter */
  public static final int PATH_UPDATE = 1;
  /** parameter update: send new parameters */
  public static final int PARAMETER_UPDATE = 2;
  /** control update: receive this from MPC program */
  public static final int CONTROL_UPDATE = 3;
  // executable location and name
  public final static String BINARY = "nativeMPC";
  public final static String RELATIVEPATH = "/src_MATLAB/MPCGokart/ForcesMPCPathFollowing/";
  /** executable location and name */
  private final static String RELATIVE_BINARY = "src_MATLAB/MPCGokart/ForcesMPCPathFollowing/nativeMPC";

  public static Optional<File> binary() {
    File file = new File(RELATIVE_BINARY).getAbsoluteFile();
    return Optional.ofNullable(file.isFile() ? file : null);
  }
}
