// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

/** module tested on 20180427 */
public class SysidSignalsModule extends AbstractModule {
  private final SysidSignalsComponent sysidSignalsComponent = new SysidSignalsComponent();
  private final JFrame jFrame = new JFrame("Signals for System Identification Rimo");
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override // from AbstractModule
  protected void first() throws Exception {
    jFrame.setContentPane(sysidSignalsComponent.getScrollPane());
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        // sysidSignalsComponent.shutdown();
      }
    });
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
    sysidSignalsComponent.sysidRimoModule.first();
  }

  @Override // from AbstractModule
  protected void last() {
    sysidSignalsComponent.sysidRimoModule.last();
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  public static void standalone() throws Exception {
    SysidSignalsModule sysidSignalsModule = new SysidSignalsModule();
    sysidSignalsModule.first();
    sysidSignalsModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
