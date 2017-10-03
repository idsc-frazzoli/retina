// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Optional;

import javax.swing.JSlider;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetTire;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SliderExt;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;

class RimoComponent extends AutoboxTestingComponent<RimoGetEvent, RimoPutEvent> {
  private final SpinnerLabel<Word> spinnerLabelLCmd = new SpinnerLabel<>();
  private final SliderExt sliderExtLVel;
  private final SpinnerLabel<Word> spinnerLabelRCmd = new SpinnerLabel<>();
  private final SliderExt sliderExtRVel;
  private final RimoGetFields rimoGetFieldsL = new RimoGetFields();
  private final RimoGetFields rimoGetFieldsR = new RimoGetFields();
  /** default message used only for display information */
  private RimoGetEvent rimoGetEvent;

  public RimoComponent() {
    // LEFT
    {
      JToolBar jToolBar = createRow("LEFT command");
      spinnerLabelLCmd.setList(RimoPutTire.COMMANDS);
      spinnerLabelLCmd.setValueSafe(RimoPutTire.OPERATION);
      spinnerLabelLCmd.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("LEFT speed");
      sliderExtLVel = SliderExt.wrap(new JSlider(-RimoPutTire.MAX_SPEED, RimoPutTire.MAX_SPEED, 0));
      sliderExtLVel.addToComponent(jToolBar);
    }
    // RIGHT
    {
      JToolBar jToolBar = createRow("RIGHT command");
      spinnerLabelRCmd.setList(RimoPutTire.COMMANDS);
      spinnerLabelRCmd.setValueSafe(RimoPutTire.OPERATION);
      spinnerLabelRCmd.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("RIGHT speed");
      sliderExtRVel = SliderExt.wrap(new JSlider(-RimoPutTire.MAX_SPEED, RimoPutTire.MAX_SPEED, 0));
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
    rimoGetFields.jTF_temperature_motor = createReading(side + " temp. motor");
    rimoGetFields.jTF_temperature_heatsink = createReading(side + " temp. heatsink");
  }

  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
    rimoGetFieldsL.updateText(rimoGetEvent.getL);
    rimoGetFieldsR.updateText(rimoGetEvent.getR);
    {
      Scalar temp = rimoGetEvent.getL.getTemperatureMotor();
      rimoGetFieldsL.jTF_temperature_motor.setText(temp.toString());
      Scalar scalar = RimoGetTire.TEMPERATURE_RANGE.rescale(temp);
      Tensor vector = ColorDataGradients.TEMPERATURE_LIGHT.apply(scalar);
      Color color = ColorFormat.toColor(vector);
      rimoGetFieldsL.jTF_temperature_motor.setBackground(color);
    }
    {
      Scalar temp = rimoGetEvent.getR.getTemperatureMotor();
      rimoGetFieldsL.jTF_temperature_motor.setText(temp.toString());
      Scalar scalar = RimoGetTire.TEMPERATURE_RANGE.rescale(temp);
      Tensor vector = ColorDataGradients.TEMPERATURE_LIGHT.apply(scalar);
      Color color = ColorFormat.toColor(vector);
      rimoGetFieldsR.jTF_temperature_motor.setBackground(color);
    }
  }

  @Override
  public void putEvent(RimoPutEvent rimoPutEvent) {
    /** as long as there is only 1 valid command word,
     * there is no need to update the spinner label */
    sliderExtLVel.jSlider.setValue(rimoPutEvent.putL.getRateRaw());
    sliderExtRVel.jSlider.setValue(rimoPutEvent.putR.getRateRaw());
    rimoGetFieldsL.updateRateColor(rimoPutEvent.putL, rimoGetEvent.getL);
    rimoGetFieldsR.updateRateColor(rimoPutEvent.putR, rimoGetEvent.getR);
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    return Optional.of(new RimoPutEvent( //
        new RimoPutTire(spinnerLabelLCmd.getValue(), (short) sliderExtLVel.jSlider.getValue()), //
        new RimoPutTire(spinnerLabelRCmd.getValue(), (short) sliderExtRVel.jSlider.getValue())));
  }
}
