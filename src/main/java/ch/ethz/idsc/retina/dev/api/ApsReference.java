// code by jph
package ch.ethz.idsc.retina.dev.api;

import ch.ethz.idsc.retina.dev.davis240c.ApsDavisEvent;

public interface ApsReference {
  public ApsDavisEvent encodeAps(int time, int x, int y, int adc);
}
