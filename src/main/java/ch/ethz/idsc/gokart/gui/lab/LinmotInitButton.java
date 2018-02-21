// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutListener;

/** gui element to initiate calibration procedure of linmot break */
/* package */ class LinmotInitButton implements LinmotPutListener, LinmotGetListener {
  private final JButton jButton = new JButton("Init");
  private LinmotGetEvent _getEvent;

  public LinmotInitButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(event -> LinmotCalibrationProvider.INSTANCE.schedule());
  }

  @Override
  public void putEvent(LinmotPutEvent putEvent) {
    jButton.setEnabled(isEnabled());
  }

  @Override
  public void getEvent(LinmotGetEvent getEvent) {
    _getEvent = getEvent;
  }

  private boolean isEnabled() {
    boolean nonOperational = Objects.isNull(_getEvent) || !_getEvent.isOperational();
    return LinmotCalibrationProvider.INSTANCE.isIdle() && nonOperational;
  }

  public JComponent getComponent() {
    return jButton;
  }
}
