// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.steer.SteerParameters;
import ch.ethz.idsc.retina.util.data.TensorProperties;

class PropertiesComponent extends ToolbarsComponent {
  private final Object object;
  private final Map<Field, JTextField> map = new HashMap<>();

  private Properties updateInstance() {
    Properties properties = new Properties();
    for (Entry<Field, JTextField> entry : map.entrySet())
      properties.setProperty(entry.getKey().getName(), entry.getValue().getText());
    TensorProperties.insert(properties, object);
    return properties;
  }

  public PropertiesComponent(Object object) {
    this.object = object;
    {
      JToolBar jToolBar = createRow("Actions");
      {
        JButton jButton = new JButton("udpate");
        jButton.addActionListener(e -> {
          updateInstance();
        });
        jToolBar.add(jButton);
      }
      {
        JButton jButton = new JButton("save");
        jButton.addActionListener(e -> {
          Properties properties = updateInstance();
          String name = object.getClass().getSimpleName() + ".properties";
          try {
            // TODO global dir
            properties.store(new FileOutputStream(new File("resources/properties", name)), null);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        });
        jToolBar.add(jButton);
      }
    }
    addSeparator();
    for (Field field : object.getClass().getFields()) {
      if (TensorProperties.isTracked(field))
        try {
          JToolBar jToolBar = createRow(field.getName());
          JTextField jTextField = new JTextField();
          map.put(field, jTextField);
          jTextField.setPreferredSize(new Dimension(180, 28));
          jTextField.setText("" + field.get(object));
          jToolBar.add(jTextField);
        } catch (Exception exception) {
          // ---
        }
    }
  }

  public static void main(String[] args) {
    JFrame jFrame = new JFrame();
    PropertiesComponent pc = new PropertiesComponent(SteerParameters.GLOBAL);
    jFrame.setContentPane(pc.getScrollPane());
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 400, 300);
    jFrame.setVisible(true);
  }
}
