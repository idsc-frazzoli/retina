// code by jph
package ch.ethz.idsc.retina.dev.lidar.vlp16;

import java.util.Objects;

public class AzimuthExtrapolation {
  public static final int FULL = 36000; // 360 degree
  // ---
  /** the default value 20 is used only for the first extrapolation the value 20
   * was chosen because it fits with the example data */
  private int half = 20;
  private Integer last;

  public void now(int azimuth) {
    if (FULL <= azimuth) // TODO remove consistency check after working release
      throw new RuntimeException("azimuth=" + azimuth);
    if (Objects.isNull(last))
      last = azimuth;
    else //
    if (last != azimuth) {
      int delta = azimuth - last;
      if (delta < 0)
        delta += FULL;
      half = delta / 2;
      // System.out.println("update " + half);
      last = azimuth;
    }
  }

  public int gap() {
    int gap = last + half;
    return gap < FULL ? gap : gap - FULL;
  }
}
