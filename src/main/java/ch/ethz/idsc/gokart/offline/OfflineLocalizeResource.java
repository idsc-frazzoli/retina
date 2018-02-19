// code by jph
package ch.ethz.idsc.gokart.offline;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;

public interface OfflineLocalizeResource {
  File file();

  Tensor model();
}
