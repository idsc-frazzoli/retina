// code by jph
package ch.ethz.idsc.retina.davis._240c;

import java.io.Serializable;

import ch.ethz.idsc.retina.davis.DavisEvent;

public class DavisApsEvent implements DavisEvent, Serializable {
  /** time from the chip hardware */
  public final int time;
  /** x ranges from [0, 1, ..., 239] */
  public final int x;
  /** y ranges from [0, 1, ..., 179] */
  public final int y;
  /** adc ranges typically in the interval [0, ..., 1023] where 0 encodes dark, and
   * 1023 brightest */
  private final int adc;

  public DavisApsEvent(int time, int x, int y, int adc) {
    this.time = time;
    this.x = x;
    this.y = y;
    this.adc = adc;
  }

  /** @return 8 most significant bits of adc reading */
  public byte grayscale() {
    return (byte) (adc >> 2);
  }

  /** @param ref
   * @return 8 most significant bits of adc reading */
  public byte grayscale(int ref) {
    return (byte) (Math.max(0, adc - ref) >> 2);
  }

  @Override
  public int time() {
    return time;
  }

  public int adc() {
    return adc;
  }

  @Override
  public String toString() { // function will be removed after debug phase
    return String.format("aps %8d  (%4d, %3d) %4d", time, x, y, adc);
  }
}
