// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

/** AutoboxCompactModule facilitates the initialization of the actuators and the
 * monitoring of the manual control providers and sensors vital for the operation
 * 
 * module first tested on 20180427 */
public class AutoboxCompactModule extends AbstractModule {
  private final AutoboxCompactComponent autoboxCompactComponent = new AutoboxCompactComponent();
  private final Timer timer = new Timer();
  private final JFrame jFrame = new JFrame(getClass().getSimpleName());
  private final WindowConfiguration windowConfiguration = AppCustomization.load(getClass(), new WindowConfiguration());

  @Override // from AbstractModule
  protected void first() {
    autoboxCompactComponent.start();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        autoboxCompactComponent.update();
      }
    }, 100, 50);
    // ---
    jFrame.setContentPane(autoboxCompactComponent.getScrollPane());
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        autoboxCompactComponent.stop();
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

  public static void standalone() throws Exception {
    AutoboxCompactModule autoboxCompactModule = new AutoboxCompactModule();
    autoboxCompactModule.first();
    autoboxCompactModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
