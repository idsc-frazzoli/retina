// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutListener;

public class LinmotInitButton implements LinmotPutListener, LinmotGetListener {
  private final JButton jButton = new JButton("Linmot Init.");

  public LinmotInitButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(event -> LinmotCalibrationProvider.INSTANCE.schedule());
  }

  @Override
  public void putEvent(LinmotPutEvent putEvent) {
    jButton.setEnabled(isEnabled());
  }

  LinmotGetEvent _getEvent;

  @Override
  public void getEvent(LinmotGetEvent getEvent) {
    _getEvent = getEvent;
  }

  private boolean isEnabled() {
    return LinmotCalibrationProvider.INSTANCE.isIdle() //
        && (Objects.isNull(_getEvent) || !_getEvent.isOperational());
  }

  public JComponent getComponent() {
    return jButton;
  }
}
