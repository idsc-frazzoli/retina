// code by jph
package ch.ethz.idsc.retina.sys;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

public class TabbedTaskGui {
  public final JFrame jFrame = new JFrame();
  private final JTabbedPane jTabbedPane = new JTabbedPane();
  private final Properties properties;

  public TabbedTaskGui(Properties properties) {
    this.properties = Objects.isNull(properties) ? new Properties() : properties;
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        System.out.println("closed");
        ModuleAuto.INSTANCE.terminateAll();
      }
    });
    jFrame.setContentPane(jTabbedPane);
  }

  public void tab(String title, List<Class<?>> modules) {
    jTabbedPane.addTab(title, new TaskComponent(modules, properties).jScrollPane);
  }
}
