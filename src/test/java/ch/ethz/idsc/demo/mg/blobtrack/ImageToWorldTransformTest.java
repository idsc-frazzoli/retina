// code by jph
package ch.ethz.idsc.demo.mg.blobtrack;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class ImageToWorldTransformTest extends TestCase {
  public void testSimple() {
    Tensor inputTensor = ResourceData.of("/demo/mg/DUBI12.csv");
    assertNotNull(inputTensor);
  }
}
