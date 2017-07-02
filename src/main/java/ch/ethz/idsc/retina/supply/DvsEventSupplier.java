// code by jph
package ch.ethz.idsc.retina.supply;

import java.awt.Dimension;

import ch.ethz.idsc.retina.core.DvsEvent;

/** Supplier */
public interface DvsEventSupplier {
  /** @return
   * @throws Exception */
  DvsEvent next() throws Exception;

  /** @return resolution */
  Dimension dimension();
}
