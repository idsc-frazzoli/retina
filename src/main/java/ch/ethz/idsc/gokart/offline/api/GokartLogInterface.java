// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;

public interface GokartLogInterface {
  /** @return log file */
  File file();

  /** @return pose {x[m], y[m], heading} at start of log */
  Tensor model();

  /** @return name or initials of driver, or empty string if unknown */
  String driver();
}
