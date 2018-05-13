// code by jph
package ch.ethz.idsc.retina.util.img;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.sca.Floor;
import junit.framework.TestCase;

public class ImageRotateTest extends TestCase {
  public void testGrayscale() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5, 255}}");
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    BufferedImage rotated = ImageRotate._180deg(bufferedImage);
    Tensor flipped = ImageFormat.from(rotated);
    assertEquals(flipped, Tensors.fromString("{{255, 5, 4}, {3, 2, 1}}"));
  }

  public void testColor() {
    Tensor stencil = Tensors.fromString("{{0, 2, 3}, {4, 5, 255}}");
    Tensor tensor = ArrayPlot.of(stencil, ColorDataGradients.AURORA);
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    BufferedImage rotated = ImageRotate._180deg(bufferedImage);
    Tensor flipped = ImageFormat.from(rotated);
    Tensor updown = Tensors.fromString("{{255, 5, 4}, {3, 2, 0}}");
    Tensor compar = ArrayPlot.of(updown, ColorDataGradients.AURORA);
    compar = Floor.of(compar); // color components are rounded down
    // compar = ImageFormat.from(ImageFormat.of(compar));
    assertEquals(flipped, compar);
  }
}
