// code by jph
package ch.ethz.idsc.gokart.offline.cache;

import java.io.File;
import java.io.IOException;

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
}
