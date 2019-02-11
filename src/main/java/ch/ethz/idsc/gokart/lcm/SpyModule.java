// code by niam jen wei and jph
package ch.ethz.idsc.gokart.lcm;

import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import lcm.spy.Spy;

public class SpyModule extends AbstractModule {
  private Spy spy;

  @Override
  protected void first() {
    try {
      spy = new Spy("");
      spy.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  @Override
  protected void last() {
    spy.close();
  }
}
