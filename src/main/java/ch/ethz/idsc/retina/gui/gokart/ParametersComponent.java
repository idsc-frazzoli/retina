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
import ch.ethz.idsc.retina.sys.AppResources;
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
  private final JButton jButtonUpdate = new JButton("udpate");
  private final JButton jButtonSave = new JButton("save");

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
        jButtonUpdate.addActionListener(e -> updateInstance());
        jButtonUpdate.setToolTipText("parse values in text fields into live memory");
        jToolBar.add(jButtonUpdate);
      }
      {
        jButtonSave.addActionListener(e -> {
          updateInstance();
          AppResources.save(object);
        });
        jButtonSave.setToolTipText("update values to memory, and save to disk");
        jToolBar.add(jButtonSave);
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
              jTextField.setBackground(isOk(jTextField.getText()) ? Color.WHITE : FAIL);
              checkFields();
            }
          });
          jTextField.addActionListener(e -> {
            if (checkFields())
              updateInstance();
          });
          map.put(field, jTextField);
        } catch (Exception exception) {
          // ---
        }
    }
  }

  private boolean checkFields() {
    boolean status = true;
    for (Entry<Field, JTextField> entry : map.entrySet())
      status &= isOk(entry.getValue().getText());
    jButtonUpdate.setEnabled(status);
    jButtonSave.setEnabled(status);
    return status;
  }

  private static boolean isOk(String string) {
    Tensor tensor = Tensors.fromString(string);
    return !tensor.flatten(-1) //
        .anyMatch(scalar -> scalar instanceof StringScalar);
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
