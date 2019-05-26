// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PurePursuitConfig extends PursuitConfig {
  public static final PurePursuitConfig GLOBAL = AppResources.load(new PurePursuitConfig());

  // ---
  public PurePursuitConfig() {
    /** look ahead distance for pure pursuit controller
     * 20171218: changed from 2.8[m] to 3.5[m] otherwise tracked angle is out of range too frequently
     * 20180304: changed from 3.5[m] to 3.9[m] to match with value used many times before
     * 20180929: changed from 3.9[m] to 3.5[m]
     * TODO as look ahead as decreased -> increase pure pursuit update rate also */
    lookAhead = Quantity.of(3.5, SI.METER);
  }
}
