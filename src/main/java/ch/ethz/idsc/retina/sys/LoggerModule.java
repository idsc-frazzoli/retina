// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.sys;

import java.util.Date;

import ch.ethz.idsc.retina.lcm.LcmLogProcess;

public final class LoggerModule extends AbstractModule {
  private LcmLogProcess lcmLogProcess;

  @Override
  protected void first() throws Exception {
    lcmLogProcess = LcmLogProcess.createDefault();
  }

  @Override
  protected void last() {
    // TODO try to send signal to exit gracefully
    // ... something like "Ctrl+C" via the process.inputstream
    System.out.println(new Date() + " lcm-logger: graceful destruction");
    try {
      lcmLogProcess.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}