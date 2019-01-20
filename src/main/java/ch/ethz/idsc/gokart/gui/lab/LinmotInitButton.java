// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionEvent;

import ch.ethz.idsc.gokart.dev.linmot.LinmotCalibrationProvider;

/** gui element to initiate calibration procedure of linmot break
 * 
 * button is enabled if
 * 1) linmot calibration queue is empty, and
 * 2) LinmotGetEvent was received with status not operational */
/* package */ class LinmotInitButton extends AutoboxInitButton {
  public LinmotInitButton() {
    super("Init", "calibrate linmot");
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    LinmotCalibrationProvider.INSTANCE.schedule();
  }

  @Override // from AutoboxInitButton
  boolean isEnabled() {
    return LinmotCalibrationProvider.INSTANCE.isScheduleSuggested();
  }
}
