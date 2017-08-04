// code by jph
package ch.ethz.idsc.retina.dev.api;

import ch.ethz.idsc.retina.dev.davis240c.DvsDavisEvent;

/** maps the chip raw dvs data to the standard coordinate system (x,y) where
 * (0,0) corresponds to left-upper corner, and
 * (x,0) parameterizes the first/top row
 * (0,y) parameterizes the first/left column */
public interface DvsReference {
  /** all input parameters are raw data from chip, or .aedat file
   * 
   * @param time
   * @param x
   * @param y
   * @param i
   * @return */
  public DvsDavisEvent encodeDvs(int time, int x, int y, int i);
}
