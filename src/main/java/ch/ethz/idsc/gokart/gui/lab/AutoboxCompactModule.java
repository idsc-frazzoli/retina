// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

public class AutoboxCompactModule extends AbstractModule {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private final AutoboxCompactComponent autoboxCompactComponent = new AutoboxCompactComponent();
  private final JFrame jFrame = new JFrame("Autobox Compact");
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  Timer timer = new Timer();

  @Override // from AbstractModule
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addGetListener(autoboxCompactComponent.linmotInitButton);
    LinmotSocket.INSTANCE.addPutListener(autoboxCompactComponent.linmotInitButton);
    // ---
    MiscSocket.INSTANCE.addGetListener(autoboxCompactComponent.miscResetButton);
    // ---
    SteerSocket.INSTANCE.addPutListener(autoboxCompactComponent.steerInitButton);
    // ---
    joystickLcmClient.startSubscriptions();
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
        Optional<JoystickEvent> optional = joystickLcmClient.getJoystick();
        String string = optional.isPresent() ? optional.get().toString() : ToolbarsComponent.UNKNOWN;
        autoboxCompactComponent.jTF_joystick.setText(string);
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
    joystickLcmClient.stopSubscriptions();
    // ---
    SteerSocket.INSTANCE.removePutListener(autoboxCompactComponent.steerInitButton);
    // ---
    MiscSocket.INSTANCE.removeGetListener(autoboxCompactComponent.miscResetButton);
    // ---
    LinmotSocket.INSTANCE.removeGetListener(autoboxCompactComponent.linmotInitButton);
    LinmotSocket.INSTANCE.removePutListener(autoboxCompactComponent.linmotInitButton);
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
