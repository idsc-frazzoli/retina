// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class GeodesicPursuitParams {
  public static final GeodesicPursuitParams GLOBAL = AppResources.load(new GeodesicPursuitParams());
  // ---
  public Scalar minDistance = Quantity.of(3, SI.METER);
}
