// code by jph
package ch.ethz.idsc.demo;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.core.map.DubendorfFrame;
import ch.ethz.idsc.gokart.offline.cache.CachedLog;
import ch.ethz.idsc.gokart.offline.map.BackgroundRender;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import junit.framework.TestCase;

public class VideoBackgroundTest extends TestCase {
  public void testSimple() throws IOException {
    File output = HomeDirectory.Pictures(getClass().getSimpleName() + ".png");
    assertFalse(output.exists());
    BackgroundRender.render(CachedLog._20190701T174152_00.file(), VideoBackground.DIMENSION, DubendorfFrame._20190401, output);
    assertTrue(output.isFile());
    output.delete();
    assertFalse(output.exists());
  }
}
