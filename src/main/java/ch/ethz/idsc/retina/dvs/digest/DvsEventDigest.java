// code by jph
package ch.ethz.idsc.retina.dvs.digest;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;

public interface DvsEventDigest {
  void digest(DvsEvent dvsEvent);
}
