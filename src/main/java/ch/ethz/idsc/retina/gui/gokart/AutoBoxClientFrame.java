// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

public class AutoBoxClientFrame {
  private final JFrame jFrame = new JFrame();
  private final List<InterfaceComponent> list = new LinkedList<>();
  private final JTabbedPane jTabbedPane = new JTabbedPane();
  private final Timer timer = new Timer();

  public AutoBoxClientFrame() {
    addTab(new RimoPutComponent());
    addTab(new LinmotPutComponent());
    addTab(new LinmotGetComponent());
    jTabbedPane.setSelectedIndex(2);
    // ---
    jFrame.setContentPane(jTabbedPane);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 500, 400);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        list.forEach(interfaceComponent -> interfaceComponent.connectAction(100, false));
        timer.cancel();
        System.out.println("cancel");
      }

      @Override
      public void windowClosed(WindowEvent e) {
        // ---
      }
    });
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
  }
}
