// code by rvmoos and jph
package ch.ethz.idsc.gokart.gui.lab;

import javax.swing.JSlider;
import javax.swing.JTextField;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;

/** gui elements to configure message sent for a single rear tire:
 * operation mode (2 bytes)
 * speed (2 bytes)
 * torque (2 bytes)
 * sdo message (9 bytes) */
/* package */ class RimoPutFields {
  final SpinnerLabel<Word> spinnerLabelCmd = new SpinnerLabel<>();
  final SliderExt sliderExtVel = SliderExt.wrap(new JSlider(RimoPutTire.MIN_SPEED, RimoPutTire.MAX_SPEED, 0));
  final SliderExt sliderExtTrq = SliderExt.wrap(new JSlider(RimoPutTire.MIN_TORQUE, RimoPutTire.MAX_TORQUE, 0));
  final SpinnerLabel<Word> spinnerLabelTrigger = new SpinnerLabel<>();
  final JTextField jTextfieldSdoCommand = new JTextField(5);
  final JTextField jTextfieldSdoMainIndex = new JTextField(8);
  final JTextField jTextfieldSdoSubIndex = new JTextField(5);
  final JTextField jTextfieldSdoData = new JTextField(20);

  public RimoPutFields() {
    jTextfieldSdoCommand.setToolTipText("enter value in hex notation, for instance f00d");
  }

  public RimoPutTire getPutTire() {
    RimoPutTire rimoPutTire = new RimoPutTire( //
        spinnerLabelCmd.getValue(), //
        (short) sliderExtVel.jSlider.getValue(), //
        (short) sliderExtTrq.jSlider.getValue());
    rimoPutTire.trigger = spinnerLabelTrigger.getValue().getByte();
    boolean isTriggered = rimoPutTire.trigger != 0;
    try {
      rimoPutTire.sdoCommand = (byte) Integer.parseInt(jTextfieldSdoCommand.getText(), 16);
      rimoPutTire.mainIndex = (short) Integer.parseInt(jTextfieldSdoMainIndex.getText(), 16);
      rimoPutTire.subIndex = (byte) Integer.parseInt(jTextfieldSdoSubIndex.getText(), 16);
      rimoPutTire.sdoData = Integer.parseInt(jTextfieldSdoData.getText(), 16);
    } catch (Exception exception) {
      if (isTriggered)
        System.out.println("problem: " + rimoPutTire.toSDOHexString());
    }
    return rimoPutTire;
  }

  public void updateGuiElements(RimoPutTire rimoPutTire) {
    sliderExtVel.jSlider.setValue(rimoPutTire.getRateRaw());
    sliderExtTrq.jSlider.setValue(rimoPutTire.getTorqueRaw());
  }

  public void setZero() {
    sliderExtVel.jSlider.setValue(0);
    sliderExtTrq.jSlider.setValue(0);
  }
}
