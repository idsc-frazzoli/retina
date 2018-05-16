// code by vc
package ch.ethz.idsc.gokart.core.map;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;

/**  */
public class OccGridConfig implements Serializable {
  public static final OccGridConfig GLOBAL = AppResources.load(new OccGridConfig());
  /***************************************************/
  public double P_M = 0.5;
  public double P_M_HIT = 0.85;
  public double P_THRESH = 0.5;
  /***************************************************/
}
