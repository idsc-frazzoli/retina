// code by jph
package ch.ethz.idsc.gokart.offline.api;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Tensor;

/** generate table that is typically exported to csv format */
public interface OfflineTableSupplier extends OfflineLogListener {
  /** @return table */
  Tensor getTable();
}
