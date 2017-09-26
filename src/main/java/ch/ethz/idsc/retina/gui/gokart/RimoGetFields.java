// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;

import javax.swing.JTextField;

import ch.ethz.idsc.retina.dev.rimo.RimoGetTire;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Round;

public class RimoGetFields {
  private static final Clip RATE_RANGE = Clip.function( //
      Quantity.of(-1, RimoGetTire.RATE_UNIT), //
      Quantity.of(+1, RimoGetTire.RATE_UNIT));
  // ---
  JTextField jTF_status_word; // 2
  JTextField jTF_actual_speed; // 4
  JTextField jTF_rms_motor_current; // 6
  JTextField jTF_dc_bus_voltage; // 8
  JTextField jTF_error_code; // 10
  JTextField jTF_temperature_motor; // 14
  JTextField jTF_temperature_heatsink; // 16
  // ---

  public void updateText(RimoGetTire rimoGetEvent) {
    jTF_status_word.setText(String.format("%04X", rimoGetEvent.status_word));
    jTF_actual_speed.setText(rimoGetEvent.getAngularRate().map(Round._3).toString());
    jTF_rms_motor_current.setText("" + rimoGetEvent.rms_motor_current);
    jTF_dc_bus_voltage.setText(rimoGetEvent.getBusVoltage().map(Round._1).toString());
    jTF_error_code.setText("" + rimoGetEvent.error_code);
    jTF_temperature_motor.setText(rimoGetEvent.getTemperatureMotor().toString());
    jTF_temperature_heatsink.setText(rimoGetEvent.getTemperatureHeatsink().toString());
  }

  public void updateRateColor(RimoPutTire rimoPutTire, RimoGetTire rimoGetTire) {
    Scalar scalar = rimoPutTire.getAngularRate().subtract(rimoGetTire.getAngularRate());
    Tensor vector = ColorDataGradients.THERMOMETER.apply(RATE_RANGE.rescale(scalar));
    Color color = ColorFormat.toColor(vector);
    jTF_actual_speed.setBackground(color);
  }
}
