// code by jph
package ch.ethz.idsc.retina.digest;

import ch.ethz.idsc.retina.core.DvsEvent;

public interface DvsEventDigest {
  void digest(DvsEvent dvsEvent);
}
