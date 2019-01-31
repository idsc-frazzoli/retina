// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

/* package */ abstract class MPCSteering extends MPCControlUpdateListener implements MPCStateProviderClient {
  /**
   * get the needed steering angle
   *  @param time current time [s]
   * @return wanted steering angle [CSE] */
  public abstract Scalar getSteering(Scalar time);

  /**
   * get the change rate of the needed steering angle
   *  @param time current time [s]
   * @return wanted steering angle change rate [CSE/s] */
  public abstract Scalar getDotSteering(Scalar time);
}
