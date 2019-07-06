// code by jph
package ch.ethz.idsc.gokart.gui.plg;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.tensor.Tensor;

public interface CurvePoseRenderPlugin {
  RenderInterface renderInterface(Tensor waypoints, Tensor pose);
}
