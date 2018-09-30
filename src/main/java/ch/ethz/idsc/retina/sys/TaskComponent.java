// code by jph
package ch.ethz.idsc.retina.sys;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

class TaskComponent {
  private final Map<Class<?>, JToggleButton> map = new HashMap<>();
  private final JPanel jpanel = new JPanel();
  private final JPanel top = new JPanel(new BorderLayout());
  final JScrollPane jScrollPane = new JScrollPane(top, //
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, //
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

  public TaskComponent(List<Class<?>> modules, Properties properties) {
    jpanel.setLayout(new GridLayout(modules.size(), 1));
    for (Class<?> module : modules) {
      final String key = module.getSimpleName();
      JToggleButton jToggleButton = new JToggleButton(getName(module));
      jToggleButton.addActionListener(e -> {
        if (jToggleButton.isSelected())
          ModuleAuto.INSTANCE.runOne(module);
        else
          ModuleAuto.INSTANCE.terminateOne(module);
      });
      if (properties.containsKey(key)) {
        String value = properties.getProperty(key);
        if (!value.isEmpty())
          jToggleButton.setToolTipText(value);
      }
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
    return name.endsWith(" Module") //
        ? name.substring(0, name.length() - 7)
        : name;
  }
}
