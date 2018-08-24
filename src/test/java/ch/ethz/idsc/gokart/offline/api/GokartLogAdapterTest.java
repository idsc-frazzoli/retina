// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;
import java.util.Arrays;

import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class GokartLogAdapterTest extends TestCase {
  public static final GokartLogInterface SIMPLE = //
      GokartLogAdapter.of(new File("src/test/resources/localization/vlp16.center.ray_autobox.rimo.get"));
  public static final GokartLogInterface FULL = //
      GokartLogAdapter.of(new File("src/test/resources/offline/20180419T124700_fast"));

  public void testSimple() {
    assertTrue(SIMPLE.file().exists());
    assertEquals(Dimensions.of(SIMPLE.pose()), Arrays.asList(3));
  }

  public void testFull() {
    assertTrue(FULL.file().exists());
    assertEquals(Dimensions.of(FULL.pose()), Arrays.asList(3));
    assertEquals(FULL.driver(), "abc");
  }
}
