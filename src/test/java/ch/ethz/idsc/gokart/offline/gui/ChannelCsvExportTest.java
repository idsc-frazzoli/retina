// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.util.io.DeleteDirectory;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import junit.framework.TestCase;

public class ChannelCsvExportTest extends TestCase {
  public void testSimple() throws IOException {
    File file = new File("src/test/resources/localization", "vlp16.center.pos.lcm");
    assertTrue(file.isFile());
    File target = HomeDirectory.Documents(getClass().getSimpleName());
    assertFalse(target.exists());
    ChannelCsvExport.of(file, target);
    assertTrue(12 < target.listFiles().length);
    DeleteDirectory.of(target, 1, 50);
  }
}
