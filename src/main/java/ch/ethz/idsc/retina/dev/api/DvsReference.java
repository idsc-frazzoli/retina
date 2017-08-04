// code by jph
package ch.ethz.idsc.retina.dev.api;

import ch.ethz.idsc.retina.dev.davis240c.DvsDavisEvent;

public interface DvsReference {
  public DvsDavisEvent encodeDvs(int time, int x, int y, int i);
}
