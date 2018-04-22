// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.misc.MiscIgnitionProvider;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/* package */ class MiscResetButton implements MiscGetListener, ActionListener, StartAndStoppable {
  private final JButton jButton = new JButton("Reset");

  public MiscResetButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(this);
  }

  @Override // from MiscGetListener
  public void getEvent(MiscGetEvent getEvent) {
    jButton.setEnabled(getEvent.isCommTimeout());
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    MiscIgnitionProvider.INSTANCE.schedule();
  }

  @Override // from StartAndStoppable
  public void start() {
    MiscSocket.INSTANCE.addGetListener(this);
  }

  @Override // from StartAndStoppable
  public void stop() {
    MiscSocket.INSTANCE.removeGetListener(this);
  }

  JComponent getComponent() {
    return jButton;
  }

  boolean isEnabled() {
    return jButton.isEnabled();
  }
}
