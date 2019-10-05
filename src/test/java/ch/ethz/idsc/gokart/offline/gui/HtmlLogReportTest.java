// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.cache.CachedLog;
import ch.ethz.idsc.gokart.offline.cache.CachedLogs;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import junit.framework.TestCase;

public class HtmlLogReportTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensors.empty().get(Tensor.ALL, 0);
    assertTrue(Tensors.isEmpty(tensor));
  }

  public void testSimple4() {
    Tensor tensor = Tensors.empty().get(Tensor.ALL, 4);
    assertTrue(Tensors.isEmpty(tensor));
  }

  public void testCached() throws Exception {
    CachedLog cachedLog = CachedLogs.randomSmall();
    File file = cachedLog.file();
    GokartLcmMap gokartLcmMap = new GokartLcmMap(file);
    File root = HomeDirectory.Downloads(getClass().getSimpleName());
    File target = new File(root, cachedLog.title());
    target.mkdirs();
    new HtmlLogReport(gokartLcmMap, cachedLog.title(), target);
    DeleteDirectory.of(root, 3, 60);
  }

  public void testEmpty() throws IOException {
    GokartLcmMap gokartLcmMap = new GokartLcmMap();
    File root = HomeDirectory.Downloads(getClass().getSimpleName());
    File target = new File(root, "emptylog");
    target.mkdirs();
    new HtmlLogReport(gokartLcmMap, "emptylog", target);
    DeleteDirectory.of(root, 3, 60);
  }
}
