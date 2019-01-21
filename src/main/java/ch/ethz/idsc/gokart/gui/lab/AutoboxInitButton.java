// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

abstract class AutoboxInitButton implements ActionListener {
  private final JButton jButton;

  AutoboxInitButton(String string, String toolTipText) {
    jButton = new JButton(string);
    jButton.setToolTipText(toolTipText);
    jButton.setEnabled(false);
    jButton.addActionListener(this);
  }

  final JComponent getComponent() {
    return jButton;
  }

  final void updateEnabled() {
    jButton.setEnabled(isEnabled());
  }

  abstract boolean isEnabled();
}
