// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;
import java.util.Arrays;

import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class GokartLogAdapterTest extends TestCase {
  public static final GokartLogInterface SIMPLE = //
      GokartLogAdapter.of(new File("src/test/resources/localization/vlp16.center.ray_autobox.rimo.get"));

  public void testSimple() {
    assertTrue(SIMPLE.file().exists());
    assertEquals(Dimensions.of(SIMPLE.model()), Arrays.asList(3, 3));
  }
}
