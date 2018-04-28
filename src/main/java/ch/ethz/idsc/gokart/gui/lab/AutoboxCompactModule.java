// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

/** AutoboxCompactModule facilitates the initialization of the actuators
 * and the monitoring of the joystick and Davis240C sensor
 * 
 * module tested on 20180427 */
public class AutoboxCompactModule extends AbstractModule implements DavisImuFrameListener {
  private final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final AutoboxCompactComponent autoboxCompactComponent = new AutoboxCompactComponent();
  private final JFrame jFrame = new JFrame("Autobox Compact");
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final Timer timer = new Timer();
  private int imuFrame_count = 0;

  @Override // from AbstractModule
  protected void first() throws Exception {
    autoboxCompactComponent.start();
    // ---
    davisImuLcmClient.addListener(this);
    davisImuLcmClient.startSubscriptions();
    joystickLcmProvider.startSubscriptions();
    // ---
    jFrame.setContentPane(autoboxCompactComponent.getScrollPane());
    // ---
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        private_windowClosed();
      }
    });
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Optional<JoystickEvent> optional = joystickLcmProvider.getJoystick();
        String string = optional.isPresent() ? optional.get().toString() : ToolbarsComponent.UNKNOWN;
        autoboxCompactComponent.jTF_joystick.setText(string);
        autoboxCompactComponent.jTF_davis240c.setText("#=" + imuFrame_count);
      }
    }, 100, 100);
  }

  @Override // from AbstractModule
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  private void private_windowClosed() {
    timer.cancel();
    joystickLcmProvider.stopSubscriptions();
    davisImuLcmClient.stopSubscriptions();
    // ---
    autoboxCompactComponent.stop();
  }

  @Override // from DavisImuFrameListener
  public void imuFrame(DavisImuFrame davisImuFrame) {
    ++imuFrame_count;
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
