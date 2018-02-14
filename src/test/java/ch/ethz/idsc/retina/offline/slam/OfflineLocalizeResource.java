// code by jph
package ch.ethz.idsc.retina.offline.slam;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;

public interface OfflineLocalizeResource {
  File file();

  Tensor model();
}
