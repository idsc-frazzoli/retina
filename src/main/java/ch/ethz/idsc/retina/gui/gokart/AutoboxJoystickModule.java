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
import ch.ethz.idsc.retina.lcm.joystick.GenericXboxPadLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public class AutoboxJoystickModule extends AbstractModule {
  private final AutoboxGenericXboxPadJoystick agxpj = new AutoboxGenericXboxPadJoystick();
  private final JFrame jFrame = new JFrame("joystick");

  @Override
  protected void first() throws Exception {
    GenericXboxPadLcmClient.INSTANCE.addListener(agxpj);
    // ---
    RimoSocket.INSTANCE.addProvider(agxpj.rimoPutProvider);
    LinmotSocket.INSTANCE.addProvider(agxpj.linmotPutProvider);
    SteerSocket.INSTANCE.addProvider(agxpj.steerPutProvider);
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
        spinnerLabel.addSpinnerListener(i -> agxpj.setSpeedLimit(i));
        spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "max speed limit");
      }
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    // ---
    jFrame.setContentPane(jPanel);
    jFrame.setBounds(200, 200, 200, 100);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        RimoSocket.INSTANCE.removeProvider(agxpj.rimoPutProvider);
        LinmotSocket.INSTANCE.removeProvider(agxpj.linmotPutProvider);
        SteerSocket.INSTANCE.removeProvider(agxpj.steerPutProvider);
        System.out.println("removed listeners and providers");
        GenericXboxPadLcmClient.INSTANCE.removeListener(agxpj);
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
