// code by ynager
package ch.ethz.idsc.gokart.core.map;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;

/**  */
public class MappingConfig implements Serializable {
  public static final MappingConfig GLOBAL = AppResources.load(new MappingConfig());
  /***************************************************/
  public double P_M = 0.5;
  public double P_M_HIT = 0.85;
  public double P_THRESH = 0.5;
  /***************************************************/
}
