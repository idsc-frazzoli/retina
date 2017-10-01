// code by jph
package ch.ethz.idsc.retina.util.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public final class RowPanel {
  private final GridBagLayout gridBagLayout = new GridBagLayout();
  public final JPanel jPanel = new JPanel(gridBagLayout);
  private final GridBagConstraints gridBagConstraints = new GridBagConstraints();

  public RowPanel() {
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1;
    jPanel.setOpaque(false);
  }

  public void add(JComponent jComponent) {
    ++gridBagConstraints.gridy; // initially -1
    gridBagLayout.setConstraints(jComponent, gridBagConstraints);
    jPanel.add(jComponent);
    jPanel.repaint();
  }
}
