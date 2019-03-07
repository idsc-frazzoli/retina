// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.File;
import java.util.Optional;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum MPCNative {
  ;
  // public static final int TCP_PORT = 4142;
  // public static final int TCP_SERVER_PORT = 4143;
  // public static final int MPC_HORIZON = 10;
  // public static final int INITIAL_MSG_SIZE = 100;
  public static final int PREDICTION_SIZE = 31;
  public static final int SPLINE_PREVIEW_SIZE = 10;
  /** time that the controller is allowed to operate in open loop control */
  public static final Scalar OPEN_LOOP_TIME = Quantity.of(2, SI.SECOND);
  // executable location and name
  public final static String BINARY = "nativeMPC";
  public final static String RELATIVE_PATH = "/src_MATLAB/MPCGokart/ForcesMPCPathFollowing/";
  /** executable location and name */
  private final static String RELATIVE_BINARY = "src_MATLAB/MPCGokart/ForcesMPCPathFollowing/nativeMPC";
  private final static String RELATIVE_LCM_BINARY = "src_MATLAB/MPCGokart/ForcesMPCPathFollowing/nativeMPCLCM";
  private final static String RELATIVE_LCM_BINARY_TEST = "src_MATLAB/MPCGokart/ForcesMPCPathFollowing/nativeLCMtest";

  public static Optional<File> binary() {
    File file = new File(RELATIVE_BINARY).getAbsoluteFile();
    return Optional.ofNullable(file.isFile() ? file : null);
  }

  public static Optional<File> lcmBinary() {
    File file = new File(RELATIVE_LCM_BINARY).getAbsoluteFile();
    return Optional.ofNullable(file.isFile() ? file : null);
  }

  public static Optional<File> lcmTestBinary() {
    File file = new File(RELATIVE_LCM_BINARY_TEST).getAbsoluteFile();
    return Optional.ofNullable(file.isFile() ? file : null);
  }
}
