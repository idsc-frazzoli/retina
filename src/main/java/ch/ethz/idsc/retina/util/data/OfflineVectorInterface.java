// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.tensor.Tensor;

public interface OfflineVectorInterface {
  /** Careful: function for offline use only
   * 
   * @return */
  Tensor asVector();
}
