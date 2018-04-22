// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutListener;

/** gui element to initiate calibration procedure of linmot break
 * 
 * button is enabled if
 * 1) linmot calibration queue is empty, and
 * 2) LinmotGetEvent was received with status not operational */
/* package */ class LinmotInitButton implements LinmotPutListener, LinmotGetListener, ActionListener {
  private final JButton jButton = new JButton("Init");
  /** most recent linmot get event */
  private LinmotGetEvent _getEvent;

  public LinmotInitButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(this);
  }

  @Override // from LinmotPutListener
  public void putEvent(LinmotPutEvent putEvent) {
    jButton.setEnabled(isEnabled());
  }

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent getEvent) {
    _getEvent = getEvent;
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    LinmotCalibrationProvider.INSTANCE.schedule();
  }

  /* package for testing */ boolean isEnabled() {
    boolean isReceived = Objects.nonNull(_getEvent);
    boolean isIdle = LinmotCalibrationProvider.INSTANCE.isIdle();
    return isReceived && !_getEvent.isOperational() && isIdle;
  }

  public JComponent getComponent() {
    return jButton;
  }
}
