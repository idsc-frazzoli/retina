// code by jph
package ch.ethz.idsc.retina.dev.dvs.digest;

import ch.ethz.idsc.retina.dev.dvs.core.DvsEvent;

public interface DvsEventDigest {
  void digest(DvsEvent dvsEvent);
}
