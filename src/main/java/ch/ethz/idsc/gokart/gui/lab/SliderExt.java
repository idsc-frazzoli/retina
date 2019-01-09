// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/* package */ class SliderExt implements ChangeListener {
  public static SliderExt wrap(JSlider jSlider) {
    return new SliderExt(jSlider);
  }

  public final JSlider jSlider;
  public final JLabel jLabel = new JLabel();

  private SliderExt(JSlider jSlider) {
    this.jSlider = jSlider;
    stateChanged(null);
    jSlider.addChangeListener(this);
  }

  @Override
  public void stateChanged(ChangeEvent changeEvent) {
    jLabel.setText(Integer.toString(jSlider.getValue()));
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
