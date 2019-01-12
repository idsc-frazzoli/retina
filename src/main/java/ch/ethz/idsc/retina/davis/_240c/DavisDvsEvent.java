// code by jph
package ch.ethz.idsc.retina.davis._240c;

import java.io.Serializable;

import ch.ethz.idsc.retina.davis.DavisEvent;

// TODO as class is used by Davis and Silicon eye -> find more general name
public class DavisDvsEvent implements DavisEvent, Serializable {
  /** time in [us] */
  public final int time;
  /** left-right pixel location, x=0 corresponds to far left */
  public final int x;
  /** up-down pixel location, y=0 corresponds to far up */
  public final int y;
  /** polarity: 0 represents a transition from bright to dark (jAERViewer shows 0
   * in red) 1 represents a transition from dark to bright (jAERViewer shows 1 in
   * green) */
  public final int i;

  public DavisDvsEvent(int time, int x, int y, int i) {
    this.time = time;
    this.x = x;
    this.y = y;
    this.i = i;
  }

  @Override
  public int time() {
    return time;
  }

  public boolean brightToDark() {
    return i == 0;
  }

  public boolean darkToBright() {
    return i == 1;
  }

  @Override
  public String toString() { // function will be removed after debug phase
    return String.format("dvs %8d  (%4d, %3d) %d", time, x, y, i);
  }
}
