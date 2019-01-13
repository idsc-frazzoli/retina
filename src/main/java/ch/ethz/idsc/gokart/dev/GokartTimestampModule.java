// code by jph
package ch.ethz.idsc.gokart.dev;

import java.io.File;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.time.SystemTimestamp;
import ch.ethz.idsc.tensor.io.UserName;

public class GokartTimestampModule extends AbstractModule {
  private static final String GOKART = "gokart";
  private static final File ROOT = new File("resources/hardware");

  @Override
  protected void first() throws Exception {
    if (isHardware()) {
      ROOT.mkdir();
      new File(ROOT, SystemTimestamp.asString() + "_beg").createNewFile();
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
