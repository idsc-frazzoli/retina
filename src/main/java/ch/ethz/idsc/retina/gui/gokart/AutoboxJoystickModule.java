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
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.DriveMode;
import ch.ethz.idsc.retina.lcm.joystick.GenericXboxPadLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public class AutoboxJoystickModule extends AbstractModule {
  private final AutoboxGenericXboxPadJoystick instance = new AutoboxGenericXboxPadJoystick();
  private final JFrame jFrame = new JFrame("joystick");

  @Override
  protected void first() throws Exception {
    GenericXboxPadLcmClient.INSTANCE.addListener(instance);
    // ---
    RimoSocket.INSTANCE.addProvider(instance.rimoPutProvider);
    LinmotSocket.INSTANCE.addProvider(instance.linmotPutProvider);
    SteerSocket.INSTANCE.addProvider(instance.steerPutProvider);
    SteerSocket.INSTANCE.addGetListener(instance);
    // ---
    JPanel jPanel = new JPanel(new BorderLayout());
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setFloatable(false);
      jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
      {
        SpinnerLabel<DriveMode> spinnerLabel = new SpinnerLabel<>();
        spinnerLabel.setArray(DriveMode.values());
        spinnerLabel.setValue(instance.driveMode);
        spinnerLabel.addSpinnerListener(i -> instance.driveMode = i);
        spinnerLabel.addToComponentReduced(jToolBar, new Dimension(120, 28), "drive mode");
      }
      {
        SpinnerLabel<Integer> spinnerLabel = new SpinnerLabel<>();
        spinnerLabel.setArray(0, 500, 1000, 2000, 4000, (int) RimoPutTire.MAX_SPEED);
        spinnerLabel.setValueSafe(instance.speedLimit);
        spinnerLabel.addSpinnerListener(i -> instance.speedLimit = i);
        spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "max speed limit");
      }
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    // ---
    jFrame.setContentPane(jPanel);
    jFrame.setBounds(200, 200, 200, 70);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        RimoSocket.INSTANCE.removeProvider(instance.rimoPutProvider);
        LinmotSocket.INSTANCE.removeProvider(instance.linmotPutProvider);
        SteerSocket.INSTANCE.removeProvider(instance.steerPutProvider);
        SteerSocket.INSTANCE.removeGetListener(instance);
        // ---
        System.out.println("removed listeners and providers");
        GenericXboxPadLcmClient.INSTANCE.removeListener(instance);
      }
    });
    jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }
}
