// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.steer.SteerCalibrationProvider;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutListener;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** gui element to initiate calibration procedure of steering wheel */
/* package */ class SteerInitButton implements SteerPutListener, ActionListener, StartAndStoppable {
  private final JButton jButton = new JButton("Calibration");

  public SteerInitButton() {
    jButton.setEnabled(false);
    jButton.addActionListener(this);
  }

  @Override // from SteerPutListener
  public void putEvent(SteerPutEvent putEvent) {
    boolean nonCalibrated = !SteerSocket.INSTANCE.getSteerColumnTracker().isSteerColumnCalibrated();
    boolean isEnabled = nonCalibrated && SteerCalibrationProvider.INSTANCE.isIdle();
    jButton.setEnabled(isEnabled);
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    SteerCalibrationProvider.INSTANCE.schedule();
  }

  @Override // from StartAndStoppable
  public void start() {
    SteerSocket.INSTANCE.addPutListener(this);
  }

  @Override // from StartAndStoppable
  public void stop() {
    SteerSocket.INSTANCE.removePutListener(this);
  }

  JComponent getComponent() {
    return jButton;
  }

  boolean isEnabled() {
    return jButton.isEnabled();
  }
}
