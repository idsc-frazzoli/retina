// code by jph
package ch.ethz.idsc.retina.sys;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class GuiConfig implements Serializable {
  public static final GuiConfig GLOBAL = AppResources.load(new GuiConfig());
  // ---
  public final Scalar fontSize = RealScalar.of(18);
  public final Scalar labelSize = RealScalar.of(36);

  /***************************************************/
  public Font getFont() {
    return new Font(Font.DIALOG, Font.BOLD, fontSize.number().intValue());
  }

  public int getLabelHeight() {
    return labelSize.number().intValue();
  }

  public JButton createButton(String string) {
    return layout(new JButton(string), string);
  }

  public JToggleButton createToggleButton(String string) {
    return layout(new JToggleButton(string), string);
  }

  public JLabel createLabel(String title) {
    return layout(new JLabel(title), title);
  }

  private <T extends JComponent> T layout(T jComponent, String string) {
    Font font = getFont();
    jComponent.setFont(font);
    FontMetrics fontMetrics = jComponent.getFontMetrics(font);
    int stringWidth = fontMetrics.stringWidth(string) + 20;
    jComponent.setPreferredSize(new Dimension(stringWidth, getLabelHeight()));
    return jComponent;
  }
}
