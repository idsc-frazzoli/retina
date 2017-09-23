// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;

public class AutoboxJoystickFrame {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(JoystickType.GENERIC_XBOX_PAD);

  public AutoboxJoystickFrame() {
    // joystickLcmClient.startSubscriptions();
    // TODO Auto-generated constructor stub
    // JPanel jPanel = new JPanel(new BorderLayout());
    // {
    // JToolBar jToolBar = new JToolBar();
    // jToolBar.setFloatable(false);
    // jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
    // {
    // SpinnerLabel<DriveMode> speedlimit = new SpinnerLabel<>();
    // speedlimit.setArray(DriveMode.values());
    // speedlimit.setIndex(0);
    // speedlimit.addSpinnerListener(i -> rimocomponent.setdrivemode(i));
    // speedlimit.addToComponentReduced(jToolBar, new Dimension(120, 28), "drive mode");
    // }
    // {
    // SpinnerLabel<Integer> speedlimit = new SpinnerLabel<>();
    // speedlimit.setArray(0, 500, 1000, 2000, 4000, 8000);
    // speedlimit.setIndex(2);
    // speedlimit.addSpinnerListener(i -> rimocomponent.setspeedlimit(i));
    // speedlimit.addToComponentReduced(jToolBar, new Dimension(70, 28), "max speed limit");
    // }
    // {
    // JToggleButton jToggle = new JToggleButton("Joystick");
    // jToggle.addActionListener(new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // boolean status = jToggle.isSelected();
    // for (InterfaceComponent ic : list) {
    // ic.setJoystickEnabled(status);
    // }
    // }
    // });
    // jToolBar.add(jToggle);
    // }
    // jPanel.add(jToolBar, BorderLayout.NORTH);
    // }
    // jPanel.add(jTabbedPane, BorderLayout.CENTER);
    // FIXME
    // joystickLcmClient.addListener(interfaceComponent);
  }
}
