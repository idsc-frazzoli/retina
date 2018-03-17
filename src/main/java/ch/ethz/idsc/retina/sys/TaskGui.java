// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.sys;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

/** is invoked from {@link RunGuiMain} */
public class TaskGui {
  private static final Properties EMPTY = new Properties();
  public final JFrame jFrame = new JFrame();

  public TaskGui(List<Class<?>> modules) {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        System.out.println("closing all modules");
        ModuleAuto.INSTANCE.terminateAll();
      }
    });
    // ---
    TaskComponent taskComponent = new TaskComponent(modules, EMPTY);
    JPanel jPanel = new JPanel(new BorderLayout());
    JToolBar jToolBar = new JToolBar();
    jToolBar.setFloatable(false);
    {
      jToolBar.add(new TaskManagerStatus().jButton);
    }
    {
      JButton termButton = new JButton("Terminate All");
      termButton.addActionListener(e -> taskComponent.terminateAll());
      jToolBar.add(termButton);
    }
    jPanel.add("North", jToolBar);
    jPanel.add("Center", taskComponent.jScrollPane);
    jFrame.setContentPane(jPanel);
    jFrame.setSize(300, 600);
    jFrame.setVisible(true);
  }
}
