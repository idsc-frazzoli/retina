// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;

public class AutoboxClientFrame {
  // TODO NRJ reduce max speed when using joystick in gui
  private final JFrame jFrame = new JFrame();
  private final List<InterfaceComponent> list = new LinkedList<>();
  private final JTabbedPane jTabbedPane = new JTabbedPane();
  private final Timer timer = new Timer();
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(JoystickType.GENERIC_XBOX_PAD);

  public AutoboxClientFrame() {
    addTab(new RimoComponent());
    addTab(new LinmotComponent());
    addTab(new SteerComponent());
    addTab(new MiscComponent());
    jTabbedPane.setSelectedIndex(1);
    // ---
    JPanel jPanel = new JPanel(new BorderLayout());
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setFloatable(false);
      JToggleButton jToggle = new JToggleButton("Joystick");
      jToggle.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          boolean status = jToggle.isSelected();
          for (InterfaceComponent ic : list) {
            ic.setJoystickEnabled(status);
          }
        }
      });
      jToolBar.add(jToggle);
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    jPanel.add(jTabbedPane, BorderLayout.CENTER);
    jFrame.setContentPane(jPanel);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 80, 500, 700);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        list.forEach(interfaceComponent -> interfaceComponent.connectAction(100, false));
        timer.cancel();
      }
    });
    joystickLcmClient.startSubscriptions();
    jFrame.setVisible(true);
  }

  private void addTab(InterfaceComponent interfaceComponent) {
    list.add(interfaceComponent);
    interfaceComponent.timer = timer;
    String string = interfaceComponent.getClass().getSimpleName();
    string = string.substring(0, string.length() - 9);
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.add(interfaceComponent.getComponent(), BorderLayout.NORTH);
    JScrollPane jScrollPane = new JScrollPane(jPanel);
    jTabbedPane.addTab(string, jScrollPane);
    joystickLcmClient.addListener(interfaceComponent);
  }
}
