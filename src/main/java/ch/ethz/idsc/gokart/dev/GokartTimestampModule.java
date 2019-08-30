// code by jph
package ch.ethz.idsc.gokart.dev;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.time.SystemTimestamp;
import ch.ethz.idsc.tensor.io.UserName;

/** deploys timestamps at start and end of operation on the gokart pc */
public class GokartTimestampModule extends AbstractModule {
  private static final String GOKART = "gokart";
  private static final File ROOT = new File("resources/hardware");

  @Override
  protected void first() {
    if (isHardware()) {
      ROOT.mkdir();
      try {
        new File(ROOT, SystemTimestamp.asString() + "_beg").createNewFile();
      } catch (IOException ioException) {
        // ---
      }
    }
  }

  @Override
  protected void last() {
    if (isHardware())
      try {
        new File(ROOT, SystemTimestamp.asString() + "_end").createNewFile();
      } catch (Exception exception) {
        // ---
      }
  }

  private static boolean isHardware() {
    return UserName.is(GOKART);
  }
}
