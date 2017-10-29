// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.File;

import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class SteerParameters {
  public static SteerParameters GLOBAL = //
      TensorProperties.retrieve(new File("resources/properties"), new SteerParameters());
  // ---
  public Scalar Kp = RealScalar.of(2.5); // 5
  public Scalar Kd = RealScalar.of(0.2); // 0.5 , 5 and 0.5 hit the saturation limit of 0.5
  public Scalar torqueLimit = RealScalar.of(0.5);
}
