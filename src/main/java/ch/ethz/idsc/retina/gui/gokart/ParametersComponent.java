// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.StringScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class ParametersComponent extends ToolbarsComponent {
  private static final Font FONT = new Font(Font.DIALOG_INPUT, Font.BOLD, 14);
  private static final Color FAIL = new Color(255, 192, 192);
  // ---
  private final Object object;
  private final Map<Field, JTextField> map = new HashMap<>();

  private void updateInstance() {
    Properties properties = new Properties();
    for (Entry<Field, JTextField> entry : map.entrySet())
      properties.setProperty(entry.getKey().getName(), entry.getValue().getText());
    TensorProperties.insert(properties, object);
  }

  public ParametersComponent(Object object) {
    this.object = object;
    {
      JToolBar jToolBar = createRow("Actions");
      {
        JButton jButton = new JButton("udpate");
        jButton.addActionListener(e -> updateInstance());
        jButton.setToolTipText("parse values in text fields into live memory");
        jToolBar.add(jButton);
      }
      {
        JButton jButton = new JButton("save");
        jButton.addActionListener(e -> {
          updateInstance();
          GokartResources.save(object);
        });
        jButton.setToolTipText("update values to memory, and save to disk");
        jToolBar.add(jButton);
      }
    }
    addSeparator();
    for (Field field : object.getClass().getFields()) {
      if (TensorProperties.isTracked(field))
        try {
          Object value = field.get(object);
          JTextField jTextField = createEditing(field.getName());
          jTextField.setFont(FONT);
          jTextField.setText("" + value);
          jTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
              Tensor tensor = Tensors.fromString(jTextField.getText());
              boolean nok = tensor.flatten(-1) //
                  .filter(scalar -> scalar instanceof StringScalar) //
                  .findAny().isPresent();
              jTextField.setBackground(nok ? FAIL : Color.WHITE);
            }
          });
          map.put(field, jTextField);
        } catch (Exception exception) {
          // ---
        }
    }
  }

  public static void main(String[] args) {
    JFrame jFrame = new JFrame();
    ParametersComponent pc = new ParametersComponent(SteerConfig.GLOBAL);
    jFrame.setContentPane(pc.getScrollPane());
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 400, 300);
    jFrame.setVisible(true);
  }
}
