// code by swisstrolley+ and jph
package ch.ethz.idsc.gokart.lcm;

import java.util.Date;
import java.util.Objects;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** invokes lcm logger binary as Process that records all lcm-messages
 * into binary files for later playback */
public final class LoggerModule extends AbstractModule {
  private LcmLogProcess lcmLogProcess;

  @Override // from AbstractModule
  protected void first() throws Exception {
    lcmLogProcess = LcmLogProcess.createDefault(HomeDirectory.file());
  }

  @Override // from AbstractModule
  protected void last() {
    if (Objects.nonNull(lcmLogProcess))
      try {
        System.out.println(new Date() + " lcm-logger: destruction");
        lcmLogProcess.close();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}