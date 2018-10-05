// code by swisstrolley+ and jph
package ch.ethz.idsc.gokart.dev;

import java.util.Date;
import java.util.Objects;

import ch.ethz.idsc.retina.sys.AbstractModule;

public final class Sees2LcmModule extends AbstractModule {
  private Sees2LcmProcess sees2LcmProcess;

  @Override
  protected void first() throws Exception {
    sees2LcmProcess = new Sees2LcmProcess();
  }

  @Override
  protected void last() {
    if (Objects.nonNull(sees2LcmProcess))
      try {
        System.out.println(new Date() + " seye process: destruction");
        sees2LcmProcess.close();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}