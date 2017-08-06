// code by jph
package ch.ethz.idsc.retina.dev.davis;

import ch.ethz.idsc.retina.dev.davis._240c.ApsDavisEvent;

/** maps the chip raw aps data to the standard coordinate system (x,y) where
 * (0,0) corresponds to left-upper corner, and
 * (x,0) parameterizes the first/top row
 * (0,y) parameterizes the first/left column */
public interface ApsReference {
  /** all input parameters are raw data from chip, or .aedat file
   * 
   * @param time
   * @param x
   * @param y
   * @param adc
   * @return */
  public ApsDavisEvent encodeAps(int time, int x, int y, int adc);
}
