// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

import ch.ethz.idsc.tensor.Tensor;

public interface DavisImageListener {
  void image(int time, Tensor image); // TODO API not final
}
