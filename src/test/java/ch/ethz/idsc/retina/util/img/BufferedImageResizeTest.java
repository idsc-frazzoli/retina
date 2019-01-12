// code by jph
package ch.ethz.idsc.retina.util.img;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ImageFormat;
import junit.framework.TestCase;

public class BufferedImageResizeTest extends TestCase {
  public void testSimple() {
    BufferedImage bufferedImage = ImageFormat.of(Array.zeros(100, 20, 4));
    BufferedImage image = BufferedImageResize.of(bufferedImage, .5);
    Tensor tensor = ImageFormat.from(image);
    List<Integer> list = Dimensions.of(tensor);
    assertEquals(list, Arrays.asList(50, 10, 4));
  }
}
