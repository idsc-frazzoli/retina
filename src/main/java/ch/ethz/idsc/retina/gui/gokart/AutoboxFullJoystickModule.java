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
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.GenericXboxPadLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public class AutoboxFullJoystickModule extends AbstractModule {
  private final AutoboxAbstractJoystick joystickInstance = new AutoboxFullControlJoystick();
  private final JFrame jFrame = new JFrame("joystick");

  @Override
  protected void first() throws Exception {
    GenericXboxPadLcmClient.INSTANCE.addListener(joystickInstance);
    // ---
    RimoSocket.INSTANCE.addPutProvider(joystickInstance.getRimoPutProvider());
    LinmotSocket.INSTANCE.addPutProvider(joystickInstance.linmotPutProvider);
    LinmotSocket.INSTANCE.addPutListener(joystickInstance.linmotPutListener);
    LinmotSocket.INSTANCE.addGetListener(joystickInstance.linmotGetListener);
    SteerSocket.INSTANCE.addPutProvider(joystickInstance.steerPutProvider);
    MiscSocket.INSTANCE.addPutProvider(joystickInstance.miscPutProvider);
    // ---
    JPanel jPanel = new JPanel(new BorderLayout());
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setFloatable(false);
      jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
      {
        SpinnerLabel<Integer> spinnerLabel = new SpinnerLabel<>();
        spinnerLabel.setArray(0, 500, 1000, 2000, 4000, (int) RimoPutTire.MAX_SPEED);
        spinnerLabel.setValueSafe(joystickInstance.getSpeedLimit());
        spinnerLabel.addSpinnerListener(i -> joystickInstance.setSpeedLimit(i));
        spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "max speed limit");
      }
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    // ---
    jFrame.setContentPane(jPanel);
    jFrame.setBounds(200, 200, 280, 70);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        RimoSocket.INSTANCE.removePutProvider(joystickInstance.getRimoPutProvider());
        LinmotSocket.INSTANCE.removePutProvider(joystickInstance.linmotPutProvider);
        SteerSocket.INSTANCE.removePutProvider(joystickInstance.steerPutProvider);
        MiscSocket.INSTANCE.removePutProvider(joystickInstance.miscPutProvider);
        // ---
        System.out.println("removed listeners and providers");
        GenericXboxPadLcmClient.INSTANCE.removeListener(joystickInstance);
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
