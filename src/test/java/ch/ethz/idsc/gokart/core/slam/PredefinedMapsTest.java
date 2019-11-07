// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class PredefinedMapsTest extends TestCase {
  public void testRange() {
    for (LocalizationMaps localizationMaps : LocalizationMaps.values()) {
      Tensor range = localizationMaps.getPredefinedMap().range();
      if (localizationMaps.name().startsWith("DUBILAB_"))
        Chop._08.requireClose(range, Tensors.vector(85.33333333333333, 85.33333333333333));
    }
  }

  public void testExistence() {
    for (LocalizationMaps localizationMaps : LocalizationMaps.values()) {
      BufferedImage bufferedImage = localizationMaps.getPredefinedMap().getImage();
      assertTrue(0 < bufferedImage.getWidth());
      assertTrue(0 < bufferedImage.getHeight());
    }
  }

  public void testDeterminant() {
    for (LocalizationMaps localizationMaps : LocalizationMaps.values()) {
      Tensor model2Pixel = localizationMaps.getPredefinedMap().getModel2Pixel();
      Scalar scalar = Det.of(model2Pixel);
      assertTrue(Sign.isNegative(scalar));
    }
  }

  public void testColorBlackAndWhite() {
    for (LocalizationMaps localizationMaps : LocalizationMaps.values()) {
      String string = localizationMaps.resource();
      Tensor tensor = ImageRegions.grayscale(ResourceData.of(string));
      Set<Tensor> set = tensor.flatten(1).distinct().collect(Collectors.toSet());
      assertEquals(set, new HashSet<>(Arrays.asList(RealScalar.of(0), RealScalar.of(255))));
    }
  }
}
