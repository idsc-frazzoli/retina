// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.steer.SteerCalibrationProvider;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutListener;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;

/** gui element to initiate calibration procedure of steering wheel */
/* package */ class SteerInitButton implements SteerPutListener {
  private final JButton jButton = new JButton("Calibration");

  public SteerInitButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(event -> SteerCalibrationProvider.INSTANCE.schedule());
  }

  @Override // from SteerPutListener
  public void putEvent(SteerPutEvent putEvent) {
    jButton.setEnabled(isEnabled());
  }

  private static boolean isEnabled() {
    boolean nonCalibrated = !SteerSocket.INSTANCE.getSteerColumnTracker().isSteerColumnCalibrated();
    return SteerCalibrationProvider.INSTANCE.isIdle() && nonCalibrated;
  }

  public JComponent getComponent() {
    return jButton;
  }
}
