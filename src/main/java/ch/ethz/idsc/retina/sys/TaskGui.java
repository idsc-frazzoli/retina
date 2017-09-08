// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.sys;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

/** is invoked from {@link RunGuiMain} */
public class TaskGui {
  public TaskGui(List<Class<?>> modules) {
    JFrame jframe = new JFrame();
    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    JPanel top = new JPanel(new BorderLayout());
    JPanel jpanel = new JPanel();
    for (Class<?> m : modules) {
      JToggleButton jToggleButton = new JToggleButton(getName(m));
      jToggleButton.addActionListener(e -> {
        if (jToggleButton.isSelected()) {
          ModuleAuto.runOne(m);
        } else {
          ModuleAuto.terminateOne(m);
        }
      });
      jpanel.add(jToggleButton);
    }
    jpanel.add(new TaskManagerStatus().toggle);
    JButton termButton = new JButton("Terminate ALL");
    termButton.addActionListener(e -> {
      ModuleAuto.terminateAll();
      for (Component comp : jpanel.getComponents()) {
        if (comp instanceof JToggleButton) {
          ((JToggleButton) comp).setSelected(false);
        }
      }
    });
    jpanel.add(termButton);
    jpanel.setLayout(new GridLayout(jpanel.getComponentCount(), 1));
    top.add("North", jpanel);
    JScrollPane jscrollpane = new JScrollPane(top);
    jframe.setContentPane(jscrollpane);
    jframe.setSize(300, 600);
    jframe.setVisible(true);
  }

  public String getName(Class<?> module) {
    return StringBrew.putSpaceBefCaps(module.getSimpleName());
  }
}
