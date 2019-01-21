// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionEvent;

import ch.ethz.idsc.gokart.dev.steer.SteerCalibrationProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;

/** GUI element to initiate calibration procedure of steering wheel */
/* package */ class SteerInitButton extends AutoboxInitButton {
  public SteerInitButton() {
    super("Calibration", "calibration of power steering");
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    SteerCalibrationProvider.INSTANCE.schedule();
  }

  @Override // from AutoboxInitButton
  boolean isEnabled() {
    return !SteerSocket.INSTANCE.getSteerColumnTracker().isSteerColumnCalibrated() //
        && SteerCalibrationProvider.INSTANCE.isIdle();
  }
}
