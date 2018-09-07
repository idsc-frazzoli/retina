// code by jph
package ch.ethz.idsc.gokart.offline.api;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TensorProperties;

/** an instance is typically constructed using {@link TensorProperties} */
public class GokartLogConfig {
  /** initial pose
   * 
   * Example: {41.99[m], 49.20[m], 0.4424784}
   * Default: null */
  public Tensor pose = null;
  /** name or initials of driver */
  public String driver = "";
}
