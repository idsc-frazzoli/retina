// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

import javax.swing.JSlider;
import javax.swing.JToolBar;

import ch.ethz.idsc.owly.math.car.DifferentialSpeed;
import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

public class RimoComponent extends InterfaceComponent implements RimoGetListener, SteerGetListener {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createShort("OPERATION", (short) 0x0009) //
  );
  // ---
  private TimerTask timerTask = null;
  private final SpinnerLabel<Word> spinnerLabelLCmd = new SpinnerLabel<>();
  private final SliderExt sliderExtLVel;
  private final SpinnerLabel<Word> spinnerLabelRCmd = new SpinnerLabel<>();
  private final SliderExt sliderExtRVel;
  private final RimoGetFields rimoGetFieldsL = new RimoGetFields();
  private final RimoGetFields rimoGetFieldsR = new RimoGetFields();
  // TODO check if this is good default?
  private RimoPutEvent rimoPutEventL = new RimoPutEvent((short) 0, (short) 0);
  private RimoPutEvent rimoPutEventR = new RimoPutEvent((short) 0, (short) 0);

  public RimoComponent() {
    // LEFT
    {
      JToolBar jToolBar = createRow("LEFT command");
      spinnerLabelLCmd.setList(COMMANDS);
      spinnerLabelLCmd.setValueSafe(COMMANDS.get(0));
      spinnerLabelLCmd.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("LEFT speed");
      sliderExtLVel = SliderExt.wrap(new JSlider(-RimoPutEvent.MAX_SPEED, RimoPutEvent.MAX_SPEED, 0));
      sliderExtLVel.addToComponent(jToolBar);
    }
    // RIGHT
    {
      JToolBar jToolBar = createRow("RIGHT command");
      spinnerLabelRCmd.setList(COMMANDS);
      spinnerLabelRCmd.setValueSafe(COMMANDS.get(0));
      spinnerLabelRCmd.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("RIGHT speed");
      sliderExtRVel = SliderExt.wrap(new JSlider(-RimoPutEvent.MAX_SPEED, RimoPutEvent.MAX_SPEED, 0));
      sliderExtRVel.addToComponent(jToolBar);
    }
    addSeparator();
    // reception
    assign(rimoGetFieldsL, "LEFT");
    addSeparator();
    assign(rimoGetFieldsR, "RIGHT");
  }

  private void assign(RimoGetFields rimoGetFields, String side) {
    rimoGetFields.jTF_status_word = createReading(side + " status word");
    rimoGetFields.jTF_actual_speed = createReading(side + " actual speed");
    rimoGetFields.jTF_rms_motor_current = createReading(side + " rms current");
    rimoGetFields.jTF_dc_bus_voltage = createReading(side + " dc bus voltage");
    // TODO NRJ background according to error code
    rimoGetFields.jTF_error_code = createReading(side + " error code");
    // TODO NRJ background according to temperature
    rimoGetFields.jTF_temperature_motor = createReading(side + " temp. motor");
    rimoGetFields.jTF_temperature_heatsink = createReading(side + " temp. heatsink");
  }

  @Override
  public void connectAction(int period, boolean isSelected) {
    if (isSelected) {
      RimoSocket.INSTANCE.start();
      timerTask = new TimerTask() {
        @Override
        public void run() {
          rimoPutEventL = new RimoPutEvent(//
              spinnerLabelLCmd.getValue().getShort(), //
              (short) sliderExtLVel.jSlider.getValue());
          rimoPutEventR = new RimoPutEvent( //
              spinnerLabelRCmd.getValue().getShort(), //
              (short) sliderExtRVel.jSlider.getValue());
          RimoSocket.INSTANCE.send(rimoPutEventL, rimoPutEventR);
        }
      };
      timer.schedule(timerTask, 100, period);
    } else {
      if (Objects.nonNull(timerTask)) {
        timerTask.cancel();
        timerTask = null;
      }
      RimoSocket.INSTANCE.stop();
    }
  }

  @Override
  public void rimoGet(RimoGetEvent rimoGetL, RimoGetEvent rimoGetR) {
    rimoGetFieldsL.updateText(rimoGetL);
    rimoGetFieldsR.updateText(rimoGetR);
    {
      double speedDiff = rimoPutEventL.speed - rimoGetL.actual_speed;
      Scalar scalar = RealScalar.of(speedDiff);
      scalar = Clip.function(-500, 500).apply(scalar);
      scalar = scalar.divide(RealScalar.of(1000)).add(RealScalar.of(0.5));
      Tensor vector = ColorDataGradients.THERMOMETER.apply(scalar);
      Color color = ColorFormat.toColor(vector);
      rimoGetFieldsL.jTF_actual_speed.setBackground(color);
    }
    {
      double speedDiff = rimoPutEventR.speed - rimoGetR.actual_speed;
      Scalar scalar = RealScalar.of(speedDiff);
      scalar = Clip.function(-500, 500).apply(scalar);
      scalar = scalar.divide(RealScalar.of(1000)).add(RealScalar.of(0.5));
      Tensor vector = ColorDataGradients.THERMOMETER.apply(scalar);
      Color color = ColorFormat.toColor(vector);
      rimoGetFieldsL.jTF_actual_speed.setBackground(color);
    }
    {
      rimoGetFieldsL.jTF_temperature_motor.setText(Quantity.of(rimoGetL.temperature_motor, "[C]").toString());
      double tempMotL = rimoGetL.temperature_motor;
      Scalar scalarL = RealScalar.of(tempMotL / 10);
      scalarL = Clip.unit().apply(scalarL);
      Tensor vectorL = ColorDataGradients.THERMOMETER.apply(scalarL);
      Color colorL = ColorFormat.toColor(vectorL);
      rimoGetFieldsL.jTF_temperature_motor.setBackground(colorL);
    }
    {
      rimoGetFieldsL.jTF_temperature_motor.setText(Quantity.of(rimoGetR.temperature_motor, "[C]").toString());
      double tempMotR = rimoGetR.temperature_motor;
      Scalar scalarR = RealScalar.of(tempMotR / 10);
      scalarR = Clip.unit().apply(scalarR);
      Tensor vectorR = ColorDataGradients.THERMOMETER.apply(scalarR);
      Color colorR = ColorFormat.toColor(vectorR);
      rimoGetFieldsR.jTF_temperature_motor.setBackground(colorR);
    }
  }

  @Override
  public void steerGet(SteerGetEvent steerGetEvent) {
    lastSteer = steerGetEvent;
  }

  private SteerGetEvent lastSteer = null;
  private final DifferentialSpeed dsL = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(+0.54));
  private final DifferentialSpeed dsR = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(-0.54));
  private int sign = 1;
  public int speedlimitjoystick = 1000;
  public DriveMode drivemode = DriveMode.SIMPLE_DRIVE;

  @Override
  public void joystick(JoystickEvent joystickEvent) {
    if (isJoystickEnabled()) {
      GenericXboxPadJoystick joystick = (GenericXboxPadJoystick) joystickEvent;
      switch (drivemode) {
      case SIMPLE_DRIVE:
        double wheelspeed = joystick.getRightKnobDirectionUp() * speedlimitjoystick;
        Scalar scalarL = dsL.get(RealScalar.of(wheelspeed), RealScalar.of(lastSteer.getSteeringAngle()));
        Scalar scalarR = dsR.get(RealScalar.of(wheelspeed), RealScalar.of(lastSteer.getSteeringAngle()));
        sliderExtLVel.jSlider.setValue(scalarL.number().intValue());
        sliderExtRVel.jSlider.setValue(scalarR.number().intValue());
        break;
      case FULL_CONTROL:
        if (joystick.isButtonPressedBack()) {
          sign = -1;
        }
        if (joystick.isButtonPressedStart()) {
          sign = 1;
        }
        double wheelL = joystick.getLeftSliderUnitValue();
        sliderExtLVel.jSlider.setValue((int) (wheelL * speedlimitjoystick * sign));
        double wheelR = joystick.getRightSliderUnitValue();
        sliderExtRVel.jSlider.setValue((int) (wheelR * speedlimitjoystick * sign));
        break;
      default:
        break;
      }
    }
  }

  public void setspeedlimit(int i) {
    speedlimitjoystick = i;
  }

  public void setdrivemode(DriveMode i) {
    drivemode = i;
  }
}
