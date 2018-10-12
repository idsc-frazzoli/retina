// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class PredefinedMapTest extends TestCase {
  public void testRange() {
    for (PredefinedMap predefinedMap : PredefinedMap.values()) {
      Tensor range = predefinedMap.range();
      assertTrue(Chop._08.close(range, Tensors.vector(85.33333333333333, 85.33333333333333)));
    }
  }

  public void testExistence() {
    for (PredefinedMap predefinedMap : PredefinedMap.values()) {
      BufferedImage bufferedImage = predefinedMap.getImage();
      assertTrue(0 < bufferedImage.getWidth());
      assertTrue(0 < bufferedImage.getHeight());
    }
  }

  public void testDeterminant() {
    for (PredefinedMap predefinedMap : PredefinedMap.values()) {
      Tensor model2Pixel = predefinedMap.getModel2Pixel();
      Scalar scalar = Det.of(model2Pixel);
      assertTrue(Sign.isNegative(scalar));
    }
  }
}
