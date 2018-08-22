// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;

public interface GokartLogInterface {
  /** @return log file */
  File file();

  /** @return 3x3 matrix in SE2 with pose {x[m], y[m], heading} at start of log */
  @Deprecated
  Tensor model(); // TODO function is deprecated

  /** @return pose {x[m], y[m], heading} at start of log */
  Tensor pose();

  /** @return name or initials of driver, or empty string if unknown */
  String driver();
  // TODO specify predefined map in config
}
