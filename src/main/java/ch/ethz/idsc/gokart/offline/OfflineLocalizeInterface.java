// code by jph
package ch.ethz.idsc.gokart.offline;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;

public interface OfflineLocalizeInterface {
  File file();

  Tensor model();
}
