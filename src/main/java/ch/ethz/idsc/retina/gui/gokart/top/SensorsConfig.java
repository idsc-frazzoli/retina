// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.io.Serializable;

import ch.ethz.idsc.retina.gui.gokart.GokartResources;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class SensorsConfig implements Serializable {
  public static SensorsConfig GLOBAL = GokartResources.load(new SensorsConfig());

  private SensorsConfig() {
  }

  // ---
  public Tensor urg04lx = Tensors.vector(1.2, 0.0, 0.05);
  public Tensor mark8 = Tensors.vector(-0.35, 0.0, 0.1);
  public Tensor vlp16 = Tensors.vector(-0.43, 0.0, 1.5958);
}
