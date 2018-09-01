// code by jph
package ch.ethz.idsc.gokart.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.StringScalarQ;

/** component that generically inspects a given object for fields of type
 * {@link Tensor} and {@link Scalar}. For each such field, a text field
 * is provided that allows the modification of the value. */
/* package */ class ParametersComponent extends ToolbarsComponent {
  private static final Font FONT = new Font(Font.DIALOG_INPUT, Font.BOLD, 14);
  private static final Color FAIL = new Color(255, 192, 192);
  private static final Color SYNC = new Color(255, 255, 192);
  // ---
  private final Object object;
  private Object reference;
  private final Map<Field, JTextField> map = new HashMap<>();
  private final JButton jButtonUpdate = new JButton("udpate");
  private final JButton jButtonSave = new JButton("save");

  private void updateInstance() {
    Properties properties = new Properties();
    for (Entry<Field, JTextField> entry : map.entrySet())
      properties.setProperty(entry.getKey().getName(), entry.getValue().getText());
    TensorProperties.wrap(object).set(properties);
  }

  public ParametersComponent(Object object) {
    this.object = object;
    try {
      reference = object.getClass().newInstance();
    } catch (Exception exception) {
      reference = null;
      exception.printStackTrace();
    }
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
    TensorProperties.wrap(object).fields().forEach(field -> {
      try {
        Object value = field.get(object);
        JTextField jTextField = createEditing(field.getName());
        jTextField.setFont(FONT);
        jTextField.setText("" + value);
        jTextField.addKeyListener(new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent keyEvent) {
            updateBackground(jTextField, field);
            checkFields();
          }
        });
        jTextField.addActionListener(e -> {
          if (checkFields())
            updateInstance();
        });
        updateBackground(jTextField, field);
        map.put(field, jTextField);
      } catch (Exception exception) {
        // ---
      }
    });
  }

  private void updateBackground(JTextField jTextField, Field field) {
    String string = jTextField.getText();
    boolean isOk = isOk(field, string);
    jTextField.setBackground(isOk ? Color.WHITE : FAIL);
    if (isOk)
      try {
        Object compare = field.get(reference);
        Object object = TensorProperties.parse(field, string);
        if (!compare.equals(object))
          jTextField.setBackground(SYNC);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  private boolean checkFields() {
    boolean status = true;
    for (Entry<Field, JTextField> entry : map.entrySet()) {
      Field field = entry.getKey();
      status &= isOk(field, entry.getValue().getText());
    }
    jButtonUpdate.setEnabled(status);
    jButtonSave.setEnabled(status);
    return status;
  }

  private static boolean isOk(Field field, String string) {
    Object object = TensorProperties.parse(field, string);
    if (object instanceof Tensor)
      return !StringScalarQ.any((Tensor) object);
    return Objects.nonNull(object);
  }
}
