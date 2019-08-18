// code by jph
package ch.ethz.idsc.gokart.offline.cache;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.util.io.ContentType;
import ch.ethz.idsc.retina.util.io.URLFetch;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.UserName;
import junit.framework.TestCase;

public class CachedLogTest extends TestCase {
  public void testSimple() throws Exception {
    if (UserName.is("datahaki")) {
      for (CachedLog cachedLog : CachedLog.values()) {
        File file = cachedLog.file();
        assertTrue(file.isFile());
      }
    }
  }

  public void testPing() throws IOException, InterruptedException {
    for (CachedLog cachedLog : CachedLog.values()) {
      try (URLFetch urlFetch = new URLFetch(cachedLog.url())) {
        assertTrue(1000 < urlFetch.length());
        ContentType.APPLICATION_OCTETSTREAM.require(urlFetch.contentType());
        Thread.sleep(50);
      }
    }
  }

  public void testDuplicate() throws IOException {
    try (URLFetch urlFetch = new URLFetch(CachedLog._20190404T143912_24.url())) {
      urlFetch.download(HomeDirectory.Downloads("urlfetchtest1.png"));
      try {
        urlFetch.download(HomeDirectory.Downloads("urlfetchtest2.png"));
        fail();
      } catch (Exception exception) {
        // ---
      }
    }
  }
}
