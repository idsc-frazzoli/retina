// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public class AutoboxJoystickFrame {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(JoystickType.GENERIC_XBOX_PAD);

  public AutoboxJoystickFrame() {
    // TODO need clean stop listener
    joystickLcmClient.addListener(AutoboxGenericXboxPadJoystick.INSTANCE);
    joystickLcmClient.startSubscriptions();
    // ---
    RimoSocket.INSTANCE.addProvider(AutoboxGenericXboxPadJoystick.INSTANCE.rimoPutProvider);
    LinmotSocket.INSTANCE.addProvider(AutoboxGenericXboxPadJoystick.INSTANCE.linmotPutProvider);
    SteerSocket.INSTANCE.addProvider(AutoboxGenericXboxPadJoystick.INSTANCE.steerPutProvider);
    // ---
    JPanel jPanel = new JPanel(new BorderLayout());
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setFloatable(false);
      jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
      {
        SpinnerLabel<Integer> spinnerLabel = new SpinnerLabel<>();
        spinnerLabel.setArray(0, 500, 1000, 2000, 4000, (int) RimoPutTire.MAX_SPEED);
        spinnerLabel.setIndex(2);
        spinnerLabel.addSpinnerListener(i -> AutoboxGenericXboxPadJoystick.INSTANCE.setspeedlimit(i));
        spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "max speed limit");
      }
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    // ---
    JFrame jFrame = new JFrame("joystick");
    jFrame.setBounds(200, 200, 200, 100);
    jFrame.setContentPane(jPanel);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        RimoSocket.INSTANCE.removeProvider(AutoboxGenericXboxPadJoystick.INSTANCE.rimoPutProvider);
        LinmotSocket.INSTANCE.removeProvider(AutoboxGenericXboxPadJoystick.INSTANCE.linmotPutProvider);
        SteerSocket.INSTANCE.removeProvider(AutoboxGenericXboxPadJoystick.INSTANCE.steerPutProvider);
        System.out.println("removed listeners and providers");
      }
    });
    jFrame.setVisible(true);
  }
}
