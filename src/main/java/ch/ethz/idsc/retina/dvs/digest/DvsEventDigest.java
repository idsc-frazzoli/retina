// code by jph
package ch.ethz.idsc.retina.dvs.digest;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;

@FunctionalInterface
public interface DvsEventDigest {
  void digest(DvsEvent dvsEvent);
}
