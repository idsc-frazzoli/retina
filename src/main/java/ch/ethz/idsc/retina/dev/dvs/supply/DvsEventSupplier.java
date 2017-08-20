// code by jph
package ch.ethz.idsc.retina.dev.dvs.supply;

import java.awt.Dimension;

import ch.ethz.idsc.retina.dev.dvs.core.DvsEvent;

/** Supplier */
public interface DvsEventSupplier {
  /** @return
   * @throws Exception */
  DvsEvent next() throws Exception;

  /** @return resolution */
  @Deprecated
  Dimension dimension();
}
