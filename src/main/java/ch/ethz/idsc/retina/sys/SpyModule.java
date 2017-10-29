// code by niam jen wei and jph
package ch.ethz.idsc.retina.sys;

import javax.swing.WindowConstants;

import lcm.spy.Spy;

public class SpyModule extends AbstractModule {
  private Spy spy;

  @Override
  protected void first() throws Exception {
    spy = new Spy("");
    spy.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
  }

  @Override
  protected void last() {
    spy.close();
  }
}
