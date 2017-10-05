// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.steer.SteerCalibrationProvider;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutListener;

public class SteerInitButton implements SteerPutListener {
  private final JButton jButton = new JButton("Steer Calib.");

  public SteerInitButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(event -> SteerCalibrationProvider.INSTANCE.schedule());
  }

  @Override
  public void putEvent(SteerPutEvent putEvent) {
    jButton.setEnabled(SteerCalibrationProvider.INSTANCE.isIdle());
  }

  public JComponent getComponent() {
    return jButton;
  }
}
