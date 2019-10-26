// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.gokart.core.slam.LocalizationMaps;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ViewLcmFrameTest extends TestCase {
  public void testSimple() {
    Tensor model2pixel = Tensors.matrix(new Number[][] { //
        { 7.5, 0, 0 }, //
        { 0, -7.5, 640 }, //
        { 0, 0, 1 } });
    for (LocalizationMaps lm : LocalizationMaps.values()) {
      if (lm.name().startsWith("DUBILAB_"))
        Chop._12.requireClose(lm.getPredefinedMap().getModel2Pixel(), model2pixel);
    }
  }
}
