// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

public class AutoBoxClientFrame {
  private final JFrame jFrame = new JFrame();
  private final JTabbedPane jTabbedPane = new JTabbedPane();

  public AutoBoxClientFrame() {
    {
      addTab(new RimoLComponent());
      addTab(new RimoRComponent());
      addTab(new LinmotComponent());
    }
    jFrame.setContentPane(jTabbedPane);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 500, 400);
    jFrame.setVisible(true);
  }

  private void addTab(InterfaceComponent interfaceComponent) {
    String string = interfaceComponent.getClass().getSimpleName();
    string = string.substring(0, string.length() - 9);
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.add(interfaceComponent.getComponent(), BorderLayout.NORTH);
    JScrollPane jScrollPane = new JScrollPane(jPanel);
    jTabbedPane.addTab(string, jScrollPane);
  }
}
