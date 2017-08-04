// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

public class DvsDavisEvent {
  /** time in [us] */
  public final int time;
  public final int x;
  public final int y;
  /** polarity */
  public final int i;

  public DvsDavisEvent(int time, int x, int y, int i) {
    this.time = time;
    this.x = x;
    this.y = y;
    this.i = i;
  }

  @Override
  public String toString() { // function will be removed after debug phase
    return String.format("dvs %8d  (%4d, %3d) %d", time, x, y, i);
  }
}
