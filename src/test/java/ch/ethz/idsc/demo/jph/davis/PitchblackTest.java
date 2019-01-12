// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class PitchblackTest extends TestCase {
  public void testDimensions() throws Exception {
    Tensor image0 = ResourceData.of("/davis/" + DavisSerial.FX2_02460045.name() + "/pitchblack.png");
    assertEquals(Dimensions.of(image0), Arrays.asList(180, 240));
  }
}
