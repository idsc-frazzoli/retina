// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;

public class AutoboxClientFrame {
  private final JFrame jFrame = new JFrame();
  private final List<InterfaceComponent> list = new LinkedList<>();
  private final JTabbedPane jTabbedPane = new JTabbedPane();

  public AutoboxClientFrame() {
    {
      RimoComponent rimocomponent = new RimoComponent();
      RimoSocket.INSTANCE.addListener(rimocomponent);
      addTab(rimocomponent);
    }
    {
      LinmotComponent linmotComponent = new LinmotComponent();
      LinmotSocket.INSTANCE.addListener(linmotComponent);
      addTab(linmotComponent);
    }
    {
      SteerComponent steerComponent = new SteerComponent();
      SteerSocket.INSTANCE.addListener(steerComponent);
      addTab(steerComponent);
    }
    {
      MiscComponent miscComponent = new MiscComponent();
      MiscSocket.INSTANCE.addListener(miscComponent);
      addTab(miscComponent);
    }
    jTabbedPane.setSelectedIndex(0);
    // ---
    jFrame.setContentPane(jTabbedPane);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 80, 500, 700);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
      }
    });
    jFrame.setVisible(true);
  }

  private void addTab(InterfaceComponent interfaceComponent) {
    list.add(interfaceComponent);
    String string = interfaceComponent.getClass().getSimpleName();
    string = string.substring(0, string.length() - 9);
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.add(interfaceComponent.getComponent(), BorderLayout.NORTH);
    JScrollPane jScrollPane = new JScrollPane(jPanel);
    jTabbedPane.addTab(string, jScrollPane);
  }
}
