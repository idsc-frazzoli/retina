// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutConfiguration;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SliderExt;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.sca.Round;

class LinmotComponent extends AutoboxTestingComponent<LinmotGetEvent, LinmotPutEvent> {
  private final JButton initButton = new JButton("Init");
  private final SpinnerLabel<Word> spinnerLabelCtrl = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelHdr = new SpinnerLabel<>();
  private final SliderExt sliderExtTPos;
  private final SliderExt sliderExtMVel;
  private final SliderExt sliderExtAcc;
  private final SliderExt sliderExtDec;
  // ---
  private final JTextField jTextFieldStatusWord;
  private final JTextField jTextFieldStateVariable;
  private final JTextField jTextFieldActualPosition;
  private final JTextField jTextFieldDemandPosition;
  private final JTextField jTextFieldWindingTemp1;
  private final JTextField jTextFieldWindingTemp2;

  public LinmotComponent() {
    {
      JToolBar jToolBar = createRow("Special Routines");
      initButton.setEnabled(false);
      initButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
          if (LinmotCalibrationProvider.INSTANCE.isIdle())
            LinmotCalibrationProvider.INSTANCE.schedule();
          else
            System.out.println("queue not empty yet");
        }
      });
      jToolBar.add(initButton);
    }
    {
      JToolBar jToolBar = createRow("control word");
      spinnerLabelCtrl.setList(LinmotPutConfiguration.COMMANDS);
      spinnerLabelCtrl.setValueSafe(LinmotPutConfiguration.COMMANDS.get(1));
      spinnerLabelCtrl.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("motion cmd hdr");
      spinnerLabelHdr.setList(LinmotPutConfiguration.HEADER);
      spinnerLabelHdr.setValueSafe(LinmotPutConfiguration.HEADER.get(0));
      spinnerLabelHdr.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // target pos
      JToolBar jToolBar = createRow("target pos");
      sliderExtTPos = SliderExt.wrap(new JSlider( //
          LinmotPutConfiguration.TARGETPOS_MIN, //
          LinmotPutConfiguration.TARGETPOS_MAX, //
          LinmotPutConfiguration.TARGETPOS_INIT));
      sliderExtTPos.addToComponent(jToolBar);
      // sliderExtF2.setValueShort(init.target_position);
    }
    { // max velocity
      JToolBar jToolBar = createRow("max velocity");
      sliderExtMVel = SliderExt.wrap(new JSlider( //
          LinmotPutConfiguration.MAXVELOCITY_MIN, //
          LinmotPutConfiguration.MAXVELOCITY_MAX, //
          LinmotPutConfiguration.MAXVELOCITY_INIT));
      sliderExtMVel.addToComponent(jToolBar);
    }
    { // acceleration
      JToolBar jToolBar = createRow("acceleration");
      sliderExtAcc = SliderExt.wrap(new JSlider( //
          LinmotPutConfiguration.ACCELERATION_MIN, //
          LinmotPutConfiguration.ACCELERATION_MAX, //
          LinmotPutConfiguration.ACCELERATION_INIT));
      sliderExtAcc.addToComponent(jToolBar);
    }
    { // deceleration
      JToolBar jToolBar = createRow("deceleration");
      sliderExtDec = SliderExt.wrap(new JSlider( //
          LinmotPutConfiguration.DECELERATION_MIN, //
          LinmotPutConfiguration.DECELERATION_MAX, //
          LinmotPutConfiguration.DECELERATION_INIT));
      sliderExtDec.addToComponent(jToolBar);
    }
    addSeparator();
    {
      jTextFieldStatusWord = createReading("status word");
      jTextFieldStateVariable = createReading("state variable");
      jTextFieldActualPosition = createReading("actual pos.");
      jTextFieldDemandPosition = createReading("demand pos.");
      jTextFieldWindingTemp1 = createReading("winding temp.1");
      jTextFieldWindingTemp2 = createReading("winding temp.2");
    }
  }

  @Override
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    jTextFieldStatusWord.setText(String.format("%04X", linmotGetEvent.status_word));
    jTextFieldStateVariable.setText(String.format("%04X", linmotGetEvent.state_variable));
    // TODO NRJ figure out units for position
    jTextFieldActualPosition.setText("" + linmotGetEvent.actual_position);
    jTextFieldDemandPosition.setText("" + linmotGetEvent.demand_position);
    // TODO simplify using new Clip API
    {
      Scalar temp = linmotGetEvent.getWindingTemperature1();
      jTextFieldWindingTemp1.setText(temp.map(Round._1).toString());
      Scalar value = LinmotGetEvent.TEMPERATURE_RANGE.rescale(temp);
      Tensor vector = ColorDataGradients.THERMOMETER.apply(value);
      Color color = ColorFormat.toColor(vector);
      jTextFieldWindingTemp1.setBackground(color);
    }
    {
      Scalar temp = linmotGetEvent.getWindingTemperature2();
      jTextFieldWindingTemp2.setText(temp.map(Round._1).toString());
      Scalar value = LinmotGetEvent.TEMPERATURE_RANGE.rescale(temp);
      Tensor vector = ColorDataGradients.THERMOMETER.apply(value);
      Color color = ColorFormat.toColor(vector);
      jTextFieldWindingTemp2.setBackground(color);
    }
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    LinmotPutEvent linmotPutEvent = //
        new LinmotPutEvent(spinnerLabelCtrl.getValue(), spinnerLabelHdr.getValue());
    linmotPutEvent.target_position = (short) sliderExtTPos.jSlider.getValue();
    linmotPutEvent.max_velocity = (short) sliderExtMVel.jSlider.getValue();
    linmotPutEvent.acceleration = (short) sliderExtAcc.jSlider.getValue();
    linmotPutEvent.deceleration = (short) sliderExtDec.jSlider.getValue();
    return Optional.of(linmotPutEvent);
  }

  @Override
  public void putEvent(LinmotPutEvent linmotPutEvent) {
    initButton.setEnabled(LinmotCalibrationProvider.INSTANCE.isIdle());
    // sliderExtTPos.jSlider.setValue(linmotPutEvent.target_position);
    // sliderExtMVel.jSlider.setValue(linmotPutEvent.max_velocity);
    // sliderExtAcc.jSlider.setValue(linmotPutEvent.acceleration);
    // sliderExtDec.jSlider.setValue(linmotPutEvent.deceleration);
  }
}
