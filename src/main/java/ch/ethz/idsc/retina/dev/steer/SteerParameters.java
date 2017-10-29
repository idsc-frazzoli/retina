// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.gui.gokart.GokartResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class SteerParameters {
  public static SteerParameters GLOBAL = GokartResources.load(new SteerParameters());

  private SteerParameters() {
  }

  // ---
  public Scalar Kp = RealScalar.of(2.5); // 5
  public Scalar Kd = RealScalar.of(0.2); // 0.5 hits the saturation limit of 0.5
  public Scalar torqueLimit = RealScalar.of(0.5);
}
