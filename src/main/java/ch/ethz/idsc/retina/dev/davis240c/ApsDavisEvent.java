// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

public class ApsDavisEvent {
  public final int time;
  public final int x;
  public final int y;
  /** adc ranges typically in the interval [0, ..., 1023]
   * where 0 encodes dark, and 1023 brightest */
  private final int adc;

  public ApsDavisEvent(int time, int x, int y, int adc) {
    this.time = time;
    this.x = x;
    this.y = y;
    this.adc = adc;
  }

  public int grayscale() {
    return adc >> 2;
  }

  @Override
  public String toString() { // function will be removed after debug phase
    return String.format("aps %8d  (%4d, %3d) %4d", time, x, y, adc);
  }
}
