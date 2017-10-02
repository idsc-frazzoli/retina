// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Optional;

import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetTire;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorFormat;

class RimoComponent extends AutoboxTestingComponent<RimoGetEvent, RimoPutEvent> {
  private final RimoPutFields rimoPutFieldsL = new RimoPutFields();
  private final RimoPutFields rimoPutFieldsR = new RimoPutFields();
  private final RimoGetFields rimoGetFieldsL = new RimoGetFields();
  private final RimoGetFields rimoGetFieldsR = new RimoGetFields();
  /** default message used only for display information */
  private RimoGetEvent rimoGetEvent;

  public RimoComponent() {
    assign(rimoPutFieldsL, "LEFT");
    assign(rimoPutFieldsR, "RIGHT");
    addSeparator();
    // reception
    assign(rimoGetFieldsL, "LEFT");
    addSeparator();
    assign(rimoGetFieldsR, "RIGHT");
  }

  private void assign(RimoPutFields rimoPutFields, String side) {
    // LEFT
    {
      JToolBar jToolBar = createRow(side + " command");
      rimoPutFields.spinnerLabelCmd.setList(RimoPutTire.COMMANDS);
      rimoPutFields.spinnerLabelCmd.setValueSafe(RimoPutTire.OPERATION);
      rimoPutFields.spinnerLabelCmd.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow(side + " speed");
      rimoPutFields.sliderExtVel.addToComponent(jToolBar);
    }
    {// TRIGGER
      JToolBar jToolBar = createRow("Trigger");
      rimoPutFields.spinnerLabelTrigger.setList(RimoPutTire.TRIGGERS);
      rimoPutFields.spinnerLabelTrigger.setValueSafe(RimoPutTire.trigOff);
      rimoPutFields.spinnerLabelTrigger.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    // SDO COMMAND
    {
      JToolBar jToolBar = createRow("SDO command");
      // rimoPutFields.jTextfieldSdoCommand.setMinimumSize(new Dimension(130, 28));
      jToolBar.add(rimoPutFields.jTextfieldSdoCommand);
      rimoPutFields.jTextfieldSdoCommand.setText("0");
      jToolBar.add(rimoPutFields.jTextfieldSdoMainIndex);
      rimoPutFields.jTextfieldSdoMainIndex.setText("0");
      jToolBar.add(rimoPutFields.jTextfieldSdoSubIndex);
      rimoPutFields.jTextfieldSdoSubIndex.setText("0");
    }
    // SDO MAIN INDEX
    // JToolBar jToolBar = createRow("SDO main index");
    // SDO SUBINDEX
    {
      // JToolBar jToolBar = createRow("SDO subindex");
    }
    // SDO DATA
    {
      JToolBar jToolBar = createRow("SDO data");
      jToolBar.add(rimoPutFields.jTextfieldSdoData);
      rimoPutFields.jTextfieldSdoData.setText("0");
    }
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
      Tensor vector = Gui.INSTANCE.TEMPERATURE_LIGHT.apply(scalar);
      Color color = ColorFormat.toColor(vector);
      rimoGetFieldsL.jTF_temperature_motor.setBackground(color);
    }
    {
      Scalar temp = rimoGetEvent.getR.getTemperatureMotor();
      rimoGetFieldsL.jTF_temperature_motor.setText(temp.toString());
      Scalar scalar = RimoGetTire.TEMPERATURE_RANGE.rescale(temp);
      Tensor vector = Gui.INSTANCE.TEMPERATURE_LIGHT.apply(scalar);
      Color color = ColorFormat.toColor(vector);
      rimoGetFieldsR.jTF_temperature_motor.setBackground(color);
    }
  }

  @Override
  public void putEvent(RimoPutEvent rimoPutEvent) {
    /** as long as there is only 1 valid command word,
     * there is no need to update the spinner label */
    rimoPutFieldsL.sliderExtVel.jSlider.setValue(rimoPutEvent.putL.getRateRaw());
    rimoPutFieldsR.sliderExtVel.jSlider.setValue(rimoPutEvent.putR.getRateRaw());
    rimoGetFieldsL.updateRateColor(rimoPutEvent.putL, rimoGetEvent.getL);
    rimoGetFieldsR.updateRateColor(rimoPutEvent.putR, rimoGetEvent.getR);
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    return Optional.of(new RimoPutEvent( //
        new RimoPutTire(rimoPutFieldsL.spinnerLabelCmd.getValue(), (short) rimoPutFieldsL.sliderExtVel.jSlider.getValue()), //
        new RimoPutTire(rimoPutFieldsR.spinnerLabelCmd.getValue(), (short) rimoPutFieldsR.sliderExtVel.jSlider.getValue())));
  }
}
