// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** gui element to initiate calibration procedure of linmot break
 * 
 * button is enabled if
 * 1) linmot calibration queue is empty, and
 * 2) LinmotGetEvent was received with status not operational */
/* package */ class LinmotInitButton implements LinmotGetListener, ActionListener, StartAndStoppable {
  private final JButton jButton = new JButton("Init");

  public LinmotInitButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(this);
  }

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent getEvent) {
    jButton.setEnabled(!getEvent.isOperational() && LinmotCalibrationProvider.INSTANCE.isIdle());
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    LinmotCalibrationProvider.INSTANCE.schedule();
  }

  @Override // from StartAndStoppable
  public void start() {
    LinmotSocket.INSTANCE.addGetListener(this);
  }

  @Override // from StartAndStoppable
  public void stop() {
    LinmotSocket.INSTANCE.removeGetListener(this);
  }

  JComponent getComponent() {
    return jButton;
  }

  boolean isEnabled() {
    return jButton.isEnabled();
  }
}
