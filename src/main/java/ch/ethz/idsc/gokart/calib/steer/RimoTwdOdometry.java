// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.owl.car.core.TwdOdometry;

public class RimoTwdOdometry extends TwdOdometry {
  public static final TwdOdometry INSTANCE = new RimoTwdOdometry();

  // ---
  private RimoTwdOdometry() {
    super(RimoAxleConfiguration.rear());
  }
}
