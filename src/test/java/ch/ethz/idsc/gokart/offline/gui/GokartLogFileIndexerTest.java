// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class GokartLogFileIndexerTest extends TestCase {
  public void testSimple() throws IOException {
    File file = new File("src/test/resources/localization/vlp16.center.ray_autobox.rimo.get", "log.lcm");
    assertTrue(file.isFile());
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    assertEquals(gokartLogFileIndexer.getRasterSize(), 1);
  }

  public void testSimple2() throws IOException {
    File file = new File("src/test/resources/localization", "vlp16.center.pos.lcm");
    assertTrue(file.isFile());
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    assertEquals(gokartLogFileIndexer.getRasterSize(), 11);
  }
}
