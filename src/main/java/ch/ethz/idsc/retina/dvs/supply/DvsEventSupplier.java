// code by jph
package ch.ethz.idsc.retina.dvs.supply;

import java.awt.Dimension;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;

/** Supplier */
public interface DvsEventSupplier {
  /** @return
   * @throws Exception */
  DvsEvent next() throws Exception;

  /** @return resolution */
  @Deprecated
  Dimension dimension();
}
