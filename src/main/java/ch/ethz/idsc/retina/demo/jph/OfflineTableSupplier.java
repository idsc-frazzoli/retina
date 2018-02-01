// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Tensor;

interface OfflineTableSupplier extends OfflineLogListener {
  Tensor getTable();
}
