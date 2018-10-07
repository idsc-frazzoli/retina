// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCPathFollowingConfig {
  public static final MPCPathFollowingConfig GLOBAL = AppResources.load(new MPCPathFollowingConfig());
  /***************************************************/
  /** The limit for the norm of the acceleration */
  public Scalar maxAcceleration = Quantity.of(1, SI.ACCELERATION);
  /** The limit for the Go-kart speed */
  public Scalar maxSpeed = Quantity.of(2, SI.VELOCITY);
}
