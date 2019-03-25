// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

public abstract class AutoboxAbstractModule extends AbstractModule {
  private final Timer timer = new Timer();
  private final JFrame jFrame = new JFrame(getClass().getSimpleName());
  private final WindowConfiguration windowConfiguration = AppCustomization.load(getClass(), new WindowConfiguration());

  @Override
  protected final void first() {
    protected_first(timer, jFrame);
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  protected abstract void protected_first(Timer timer, JFrame jFrame);

  @Override
  protected final void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  protected static void standalone(AutoboxAbstractModule autoboxAbstractModule) throws Exception {
    autoboxAbstractModule.first();
    autoboxAbstractModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
