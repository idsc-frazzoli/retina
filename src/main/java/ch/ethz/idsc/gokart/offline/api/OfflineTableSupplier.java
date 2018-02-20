// code by jph
package ch.ethz.idsc.gokart.offline.api;

import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Tensor;

public interface OfflineTableSupplier extends OfflineLogListener {
  Tensor getTable();
}
