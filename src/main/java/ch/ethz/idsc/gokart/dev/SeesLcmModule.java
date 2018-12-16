// code by swisstrolley+ and jph
package ch.ethz.idsc.gokart.dev;

import java.util.Date;
import java.util.Objects;

import ch.ethz.idsc.retina.util.sys.AbstractModule;

public final class SeesLcmModule extends AbstractModule {
  private SeesLcmProcess seesLcmProcess;

  @Override
  protected void first() throws Exception {
    seesLcmProcess = new SeesLcmProcess();
  }

  @Override
  protected void last() {
    if (Objects.nonNull(seesLcmProcess))
      try {
        System.out.println(new Date() + " seye process: destruction");
        seesLcmProcess.close();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}