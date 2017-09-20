// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.util.Objects;
import java.util.TimerTask;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.Round;

public class SteerComponent extends InterfaceComponent implements SteerGetListener {
  public static final int AMP = 1000;
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SliderExt sliderExtTorque;
  private final JTextField jTextField;

  public SteerComponent() {
    {
      JToolBar jToolBar = createRow("command");
      spinnerLabelLw.setList(SteerPutEvent.COMMANDS);
      spinnerLabelLw.setValueSafe(SteerPutEvent.CMD_ON);
      spinnerLabelLw.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("torque");
      sliderExtTorque = SliderExt.wrap(new JSlider(-AMP, AMP, 0)); // values are divided by 1000
      sliderExtTorque.physics = scalar -> scalar.multiply(RealScalar.of(1e-3)).map(Round._4).Get();
      sliderExtTorque.addToComponent(jToolBar);
    }
    addSeparator();
    { // reception
      jTextField = createReading("received");
    }
  }

  private TimerTask timerTask = null;

  @Override
  public void connectAction(int period, boolean isSelected) {
    if (isSelected) {
      SteerSocket.INSTANCE.start();
      timerTask = new TimerTask() {
        @Override
        public void run() {
          SteerPutEvent steerPutEvent = new SteerPutEvent( //
              spinnerLabelLw.getValue().getByte(), //
              sliderExtTorque.jSlider.getValue() * 1e-3f);
          SteerSocket.INSTANCE.send(steerPutEvent);
        }
      };
      timer.schedule(timerTask, 100, period);
    } else {
      if (Objects.nonNull(timerTask)) {
        timerTask.cancel();
        timerTask = null;
      }
      SteerSocket.INSTANCE.stop();
    }
  }

  @Override
  public void steerGet(SteerGetEvent steerGetEvent) {
    jTextField.setText(steerGetEvent.getRemainingInHex());
  }

  @Override
  public void joystick(JoystickEvent joystickEvent) {
    if (isJoystickEnabled()) {
      GenericXboxPadJoystick joystick = (GenericXboxPadJoystick) joystickEvent;
      double value = -joystick.getRightKnobDirectionRight();
      sliderExtTorque.jSlider.setValue((int) (AMP * value));
    }
  }
}
