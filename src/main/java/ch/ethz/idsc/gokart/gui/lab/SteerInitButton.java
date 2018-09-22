// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionEvent;

import ch.ethz.idsc.retina.dev.steer.SteerCalibrationProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;

/** GUI element to initiate calibration procedure of steering wheel */
/* package */ class SteerInitButton extends AutoboxInitButton {
  public SteerInitButton() {
    super("Calibration");
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    SteerCalibrationProvider.INSTANCE.schedule();
  }

  @Override // from AutoboxInitButton
  boolean isEnabled() {
    boolean nonCalibrated = !SteerSocket.INSTANCE.getSteerColumnTracker().isSteerColumnCalibrated();
    return nonCalibrated && SteerCalibrationProvider.INSTANCE.isIdle();
  }
}
