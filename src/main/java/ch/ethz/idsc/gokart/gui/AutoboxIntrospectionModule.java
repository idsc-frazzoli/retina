// code by jph
package ch.ethz.idsc.gokart.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

public class AutoboxIntrospectionModule extends AbstractModule {
  private static final int PERIOD_MS = 200; // 200 ms -> 5 Hz
  // ---
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final Timer timer = new Timer();
  private final AutoboxIntrospectionComponent autoboxIntrospectionComponent = new AutoboxIntrospectionComponent();

  @Override // from AbstractModule
  protected void first() {
    jFrame.setContentPane(autoboxIntrospectionComponent.jPanel);
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        autoboxIntrospectionComponent.update();
      }
    };
    timer.schedule(timerTask, 100, PERIOD_MS);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        timer.cancel();
      }
    });
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  /***************************************************/
  public static void standalone() throws Exception {
    AutoboxIntrospectionModule autoboxTestingModule = new AutoboxIntrospectionModule();
    autoboxTestingModule.first();
    autoboxTestingModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
