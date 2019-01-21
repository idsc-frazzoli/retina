// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface OfflineVectorInterface {
  /** Careful: function for offline use only
   * 
   * @return vector with entries as close as possible to physical meaningful values */
  // TODO JAN rather return type Number[]
  Tensor asVector();
}
