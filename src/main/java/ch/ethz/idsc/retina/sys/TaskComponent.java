// code by jph
package ch.ethz.idsc.retina.sys;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

class TaskComponent {
  private final Map<Class<?>, JToggleButton> map = new HashMap<>();
  private final JPanel jpanel = new JPanel();
  private final JPanel top = new JPanel(new BorderLayout());
  public final JScrollPane jScrollPane = new JScrollPane(top);

  public TaskComponent(List<Class<?>> modules) {
    jpanel.setLayout(new GridLayout(modules.size(), 1));
    for (Class<?> module : modules) {
      JToggleButton jToggleButton = new JToggleButton(getName(module));
      jToggleButton.addActionListener(e -> {
        if (jToggleButton.isSelected())
          ModuleAuto.INSTANCE.runOne(module);
        else
          ModuleAuto.INSTANCE.terminateOne(module);
      });
      jpanel.add(jToggleButton);
      map.put(module, jToggleButton);
    }
    top.add("North", jpanel);
  }

  public void terminateAll() {
    for (Entry<Class<?>, JToggleButton> entry : map.entrySet()) {
      ModuleAuto.INSTANCE.terminateOne(entry.getKey());
      entry.getValue().setSelected(false);
    }
  }

  private static String getName(Class<?> module) {
    String name = StringBrew.putSpaceBefCaps(module.getSimpleName());
    return name.endsWith(" Module") ? name.substring(0, name.length() - 7) : name;
  }
}
