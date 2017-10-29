// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.Serializable;

import ch.ethz.idsc.retina.gui.gokart.GokartResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class SteerConfig implements Serializable {
  public static SteerConfig GLOBAL = GokartResources.load(new SteerConfig());

  private SteerConfig() {
  }

  // ---
  public Scalar Kp = RealScalar.of(2.5); // 5
  public Scalar Kd = RealScalar.of(0.2); // 0.5 hits the saturation limit of 0.5
  public Scalar torqueLimit = RealScalar.of(0.5);
  // ---
  /** conversion factor from measured steer column angle to front wheel angle */
  public Scalar column2steer = RealScalar.of(1.0);
}
