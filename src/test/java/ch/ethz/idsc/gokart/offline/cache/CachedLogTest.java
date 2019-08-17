// code by jph
package ch.ethz.idsc.gokart.offline.cache;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.util.io.ContentType;
import ch.ethz.idsc.retina.util.io.URLFetch;
import ch.ethz.idsc.tensor.io.UserName;
import junit.framework.TestCase;

public class CachedLogTest extends TestCase {
  public void testSimple() throws IOException {
    if (UserName.is("datahaki")) {
      for (CachedLog cachedLog : CachedLog.values()) {
        File file = cachedLog.file();
        assertTrue(file.isFile());
      }
    }
  }

  public void testPing() throws IOException, InterruptedException {
    for (CachedLog cachedLog : CachedLog.values()) {
      URLFetch urlFetch = new URLFetch(cachedLog.url(), ContentType.APPLICATION_OCTETSTREAM);
      assertTrue(urlFetch.ping());
      Thread.sleep(100);
    }
  }
}
