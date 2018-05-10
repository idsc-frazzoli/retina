// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class PredefinedMapTest extends TestCase {
  public void testSimple() {
    for (PredefinedMap predefinedMap : PredefinedMap.values()) {
      Tensor range = predefinedMap.range();
      assertTrue(Chop._08.close(range, Tensors.vector(85.33333333333333, 85.33333333333333)));
    }
  }

  public void testExistance() {
    for (PredefinedMap predefinedMap : PredefinedMap.values()) {
      BufferedImage bufferedImage = predefinedMap.getImage();
      assertTrue(0 < bufferedImage.getWidth());
      assertTrue(0 < bufferedImage.getHeight());
    }
  }
}
