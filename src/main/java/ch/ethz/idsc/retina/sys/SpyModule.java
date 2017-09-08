// code by niam jen wei and jph
package ch.ethz.idsc.retina.sys;

import lcm.spy.Spy;

public class SpyModule extends AbstractModule {
  private Spy spy;

  @Override
  protected void first() throws Exception {
    spy = new Spy("");
  }

  @Override
  protected void last() {
    spy.close();
  }
}
