// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import ch.ethz.idsc.retina.dev.RasterInterface;
import ch.ethz.idsc.retina.dev.davis.ApsReference;
import ch.ethz.idsc.retina.dev.davis.DvsReference;

public enum Davis240c implements RasterInterface, DvsReference, ApsReference {
  INSTANCE;
  // ---
  private static final int WIDTH = 240;
  private static final int HEIGHT = 180;
  private static final int LAST_X = WIDTH - 1;
  private static final int LAST_Y = HEIGHT - 1;
  private static final int ADC_MAX = 1023;

  @Override
  public int getWidth() {
    return WIDTH;
  }

  @Override
  public int getHeight() {
    return HEIGHT;
  }

  @Override
  public DvsDavisEvent encodeDvs(int time, int x, int y, int i) {
    return new DvsDavisEvent(time, LAST_X - x, LAST_Y - y, i);
  }

  @Override
  public ApsDavisEvent encodeAps(int time, int x, int y, int adc) {
    return new ApsDavisEvent(time, x, LAST_Y - y, ADC_MAX - adc);
  }
}
