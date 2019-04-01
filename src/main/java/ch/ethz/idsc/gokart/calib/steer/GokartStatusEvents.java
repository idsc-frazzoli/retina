// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.gui.GokartStatusEvent;

public enum GokartStatusEvents {
  ;
  /** uncalibrated */
  public static final GokartStatusEvent UNKNOWN = new GokartStatusEvent(Float.NaN);
}
