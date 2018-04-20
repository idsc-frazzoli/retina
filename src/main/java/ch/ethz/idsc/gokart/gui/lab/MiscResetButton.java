// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.misc.MiscIgnitionProvider;

/* package */ class MiscResetButton implements MiscGetListener {
  private final JButton jButton = new JButton("Reset");

  public MiscResetButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(event -> MiscIgnitionProvider.INSTANCE.schedule());
  }

  @Override
  public void getEvent(MiscGetEvent getEvent) {
    jButton.setEnabled(getEvent.isCommTimeout());
  }

  JComponent getComponent() {
    return jButton;
  }
}
