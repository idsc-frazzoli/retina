// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;

public interface GokartLogInterface {
  File file();

  Tensor model();
}
