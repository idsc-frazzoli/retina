// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Ordering;
import junit.framework.TestCase;

public class Vlp16PanoramaProviderTest extends TestCase {
  public void testPixelY() {
    // TODO JPH lock Vlp16PanoramaProvider.PIXEL_Y to StaticHelper
    int[] array = IntStream.range(0, 16).map(Vlp16Helper::degree).toArray();
    Tensor vector = Tensors.vectorInt(array);
    Ordering.DECREASING.of(vector);
    IntStream.of(Vlp16PanoramaProvider.PIXEL_Y).map(Vlp16Helper::degree).toArray();
  }
}
