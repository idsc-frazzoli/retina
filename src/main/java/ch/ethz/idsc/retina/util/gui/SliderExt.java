// code by jph
package ch.ethz.idsc.retina.util.gui;

import java.util.Objects;
import java.util.function.Function;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.ethz.idsc.tensor.Scalar;

public class SliderExt implements ChangeListener {
  public static SliderExt wrap(JSlider jSlider) {
    return new SliderExt(jSlider);
  }

  public final JSlider jSlider;
  public final JLabel jLabel = new JLabel();
  public Function<Integer, Scalar> physics;

  private SliderExt(JSlider jSlider) {
    this.jSlider = jSlider;
    stateChanged(null);
    jSlider.addChangeListener(this);
  }

  @Override
  public void stateChanged(ChangeEvent changeEvent) {
    StringBuilder stringBuilder = new StringBuilder();
    if (Objects.isNull(physics)) {
      stringBuilder.append(jSlider.getValue());
    } else {
      final int value = jSlider.getValue();
      Scalar scalar = physics.apply(value);
      stringBuilder.append(scalar.toString());
      String toolTip = "" + value;
      jLabel.setToolTipText(toolTip);
      jSlider.setToolTipText(toolTip);
    }
    jLabel.setText(stringBuilder.toString());
  }

  public void addToComponent(JToolBar jToolBar) {
    jToolBar.add(jSlider);
    jToolBar.add(jLabel);
  }

  public void setValueShort(short value) {
    jSlider.setValue(value);
  }

  public void setValueUnsignedShort(short value) {
    jSlider.setValue(value & 0xffff);
  }
}
