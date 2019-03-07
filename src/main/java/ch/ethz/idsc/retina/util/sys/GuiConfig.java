// code by jph
package ch.ethz.idsc.retina.util.sys;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class GuiConfig {
  public static final GuiConfig GLOBAL = AppResources.load(new GuiConfig());
  // ---
  public final Scalar fontSize = RealScalar.of(22);
  public final Scalar labelSize = RealScalar.of(46);

  /***************************************************/
  public Font getFont() {
    return new Font(Font.DIALOG, Font.BOLD, fontSize.number().intValue());
  }

  public int getLabelHeight() {
    return labelSize.number().intValue();
  }

  public JButton createButton(String string) {
    return layout(new JButton(string), string, 20);
  }

  public JToggleButton createToggleButton(String string) {
    return layout(new JToggleButton(string), string, 20);
  }

  public JLabel createLabel(String title) {
    return layout(new JLabel(title), title, 20);
  }

  public JLabel createSubLabel(String title) {
    return layout(new JLabel(title), title, 0);
  }

  private <T extends JComponent> T layout(T jComponent, String string, int margin) {
    Font font = getFont();
    jComponent.setFont(font);
    FontMetrics fontMetrics = jComponent.getFontMetrics(font);
    int stringWidth = fontMetrics.stringWidth(string) + margin;
    jComponent.setPreferredSize(new Dimension(stringWidth, getLabelHeight()));
    return jComponent;
  }
}
