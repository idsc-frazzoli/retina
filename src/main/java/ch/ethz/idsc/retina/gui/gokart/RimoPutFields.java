package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JSlider;
import javax.swing.JTextField;

import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SliderExt;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public class RimoPutFields {
  final SpinnerLabel<Word> spinnerLabelCmd = new SpinnerLabel<>();
  final SliderExt sliderExtVel = SliderExt.wrap(new JSlider(-RimoPutTire.MAX_SPEED, RimoPutTire.MAX_SPEED, 0));
  final SpinnerLabel<Word> spinnerLabelTrigger = new SpinnerLabel<>();
  final JTextField jTextfieldSdoCommand = new JTextField(5);
  final JTextField jTextfieldSdoMainIndex = new JTextField(8);
  final JTextField jTextfieldSdoSubIndex = new JTextField(5);
  final JTextField jTextfieldSdoData = new JTextField(20);

  public RimoPutTire getPutTire() {
    RimoPutTire rimoPutTire = new RimoPutTire( //
        spinnerLabelCmd.getValue(), //
        (short) sliderExtVel.jSlider.getValue());
    rimoPutTire.trigger = spinnerLabelTrigger.getValue().getByte();
    try {
      rimoPutTire.sdoCommand = (byte) Integer.parseInt(jTextfieldSdoCommand.getText());
      rimoPutTire.mainIndex = (short) Integer.parseInt(jTextfieldSdoMainIndex.getText());
      rimoPutTire.subIndex = (byte) Integer.parseInt(jTextfieldSdoSubIndex.getText());
      rimoPutTire.sdoData = Integer.parseInt(jTextfieldSdoData.getText());
    } catch (Exception exception) {
      System.out.println("cannot parse text field as number");
    }
    return rimoPutTire;
  }
}
