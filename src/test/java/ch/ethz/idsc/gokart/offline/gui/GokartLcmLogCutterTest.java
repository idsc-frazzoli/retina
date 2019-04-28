// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.cache.CachedLog;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import junit.framework.TestCase;

public class GokartLcmLogCutterTest extends TestCase {
  public void testSimple() throws IOException, InterruptedException {
    File file = new File("src/test/resources/localization", "vlp16.center.pos.lcm");
    assertTrue(file.isFile());
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    GokartLcmLogCutter gokartLcmLogCutter = //
        new GokartLcmLogCutter(gokartLogFileIndexer, HomeDirectory.file(), "some");
    Thread.sleep(200);
    gokartLcmLogCutter.jFrame.setVisible(false);
  }

  public void testCached() throws IOException, InterruptedException {
    CachedLog cachedLog = CachedLog._20190401T115537_02;
    File file = cachedLog.file();
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    GokartLcmLogCutter gokartLcmLogCutter = //
        new GokartLcmLogCutter(gokartLogFileIndexer, HomeDirectory.file(), "some");
    Thread.sleep(200);
    gokartLcmLogCutter.jFrame.setVisible(false);
  }
}
