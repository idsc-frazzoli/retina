// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutListener;

public class LinmotInitButton implements LinmotPutListener {
  private final JButton jButton = new JButton("Linmot Init.");

  public LinmotInitButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(event -> LinmotCalibrationProvider.INSTANCE.schedule());
  }

  @Override
  public void putEvent(LinmotPutEvent putEvent) {
    jButton.setEnabled(LinmotCalibrationProvider.INSTANCE.isIdle());
  }

  public JComponent getComponent() {
    return jButton;
  }
}
