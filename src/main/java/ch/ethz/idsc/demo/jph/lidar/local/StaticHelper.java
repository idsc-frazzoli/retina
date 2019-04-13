// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import ch.ethz.idsc.gokart.core.slam.Se2MultiresGrids;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum StaticHelper {
  ;
  static final String LOG_LCM = "log.lcm";
  static final String POST_LCM = "post.lcm";

  /** function strictly only for post-processing!
   * search grid is too fine for use in realtime.
   * 
   * @param fan
   * @return */
  public static Se2MultiresGrids offlineSe2MultiresGrids(int fan) {
    return new Se2MultiresGrids( //
        RealScalar.of(0.8 / fan), //
        Magnitude.ONE.apply(Quantity.of(9.0 / fan, NonSI.DEGREE_ANGLE)), //
        fan, //
        4);
  }
}
