// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Optional;

import javax.swing.JSlider;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetTire;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SliderExt;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.sca.Clip;

class RimoComponent extends AutoboxTestingComponent implements RimoGetListener, RimoPutListener {
  private final SpinnerLabel<Word> spinnerLabelLCmd = new SpinnerLabel<>();
  private final SliderExt sliderExtLVel;
  private final SpinnerLabel<Word> spinnerLabelRCmd = new SpinnerLabel<>();
  private final SliderExt sliderExtRVel;
  private final RimoGetFields rimoGetFieldsL = new RimoGetFields();
  private final RimoGetFields rimoGetFieldsR = new RimoGetFields();
  /** default message used only for display information */
  private RimoPutEvent rimoPutEvent = RimoPutEvent.STOP;

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
    RimoGetTire rimoGetL = rimoGetEvent.getL;
    RimoGetTire rimoGetR = rimoGetEvent.getR;
    rimoGetFieldsL.updateText(rimoGetL);
    rimoGetFieldsR.updateText(rimoGetR);
    rimoGetFieldsL.updateRateColor(rimoPutEvent.putL, rimoGetL);
    rimoGetFieldsR.updateRateColor(rimoPutEvent.putR, rimoGetR);
    // TODO NRJ temperature readings are always 0 !
    {
      Scalar temp = rimoGetL.getTemperatureMotor();
      rimoGetFieldsL.jTF_temperature_motor.setText(temp.toString());
      double tempMotL = temp.number().doubleValue();
      Scalar scalarL = RealScalar.of(tempMotL / 10);
      scalarL = Clip.unit().apply(scalarL);
      Tensor vectorL = ColorDataGradients.THERMOMETER.apply(scalarL);
      Color colorL = ColorFormat.toColor(vectorL);
      rimoGetFieldsL.jTF_temperature_motor.setBackground(colorL);
    }
    {
      Scalar temp = rimoGetR.getTemperatureMotor();
      rimoGetFieldsL.jTF_temperature_motor.setText(temp.toString());
      double tempMotR = temp.number().doubleValue();
      Scalar scalarR = RealScalar.of(tempMotR / 10);
      scalarR = Clip.unit().apply(scalarR);
      Tensor vectorR = ColorDataGradients.THERMOMETER.apply(scalarR);
      Color colorR = ColorFormat.toColor(vectorR);
      rimoGetFieldsR.jTF_temperature_motor.setBackground(colorR);
    }
  }

  public final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    @Override
    public Optional<RimoPutEvent> getPutEvent() {
      return Optional.of(new RimoPutEvent( //
          new RimoPutTire(spinnerLabelLCmd.getValue(), (short) sliderExtLVel.jSlider.getValue()), //
          new RimoPutTire(spinnerLabelRCmd.getValue(), (short) sliderExtRVel.jSlider.getValue())));
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.TESTING;
    }
  };

  @Override
  public void putEvent(RimoPutEvent rimoPutEvent) {
    // TODO also assign spinner labels
    this.rimoPutEvent = rimoPutEvent;
    sliderExtLVel.jSlider.setValue(rimoPutEvent.putL.getRateRaw());
    sliderExtRVel.jSlider.setValue(rimoPutEvent.putR.getRateRaw());
  }
}
