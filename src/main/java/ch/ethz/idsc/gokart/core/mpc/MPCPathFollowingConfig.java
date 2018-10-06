// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.Serializable;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCPathFollowingConfig implements Serializable {
  /** The limit for the norm of the acceleration */
  public Scalar maxAcceleration = Quantity.of(1,SI.ACCELERATION);
  /** The limit for the gokart speed */
  public Scalar maxSpeed = Quantity.of(2, SI.VELOCITY);
}
