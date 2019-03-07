// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.Color;
import java.util.Optional;

import javax.swing.JTextField;

import ch.ethz.idsc.gokart.dev.rimo.RimoEmergencyError;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetTire;
import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class RimoGetFields {
  private static final Clip RATE_RANGE = Clip.function( //
      Quantity.of(-3, SIDerived.RADIAN_PER_SECOND), //
      Quantity.of(+3, SIDerived.RADIAN_PER_SECOND));
  // ---
  JTextField jTF_status_word; // 2
  JTextField jTF_actual_speed; // 4
  JTextField jTF_rms_motor_current; // 6
  JTextField jTF_dc_bus_voltage; // 8
  JTextField jTF_error_code; // 10
  JTextField jTF_error_code_emergency; // 10
  JTextField jTF_temperature_motor; // 14
  JTextField jTF_temperature_heatsink; // 16
  JTextField jTF_SdoMessage; // 24

  // ---
  public void updateText(RimoGetTire rimoGetTire) {
    jTF_status_word.setText(String.format("%04X", rimoGetTire.status_word));
    jTF_actual_speed.setText(rimoGetTire.getAngularRate_Y().map(Round._3).toString());
    jTF_rms_motor_current.setText(Short.toString(rimoGetTire.rms_motor_current));
    jTF_dc_bus_voltage.setText(rimoGetTire.getBusVoltage().map(Round._1).toString());
    jTF_error_code.setText(String.format("%08X", rimoGetTire.error_code));
    {
      Optional<RimoEmergencyError> optional = rimoGetTire.getEmergencyError();
      jTF_error_code_emergency.setText(optional.isPresent() ? optional.get().name() : "");
    }
    jTF_temperature_motor.setText(rimoGetTire.getTemperatureMotor().toString());
    jTF_temperature_heatsink.setText(rimoGetTire.getTemperatureHeatsink().toString());
    jTF_SdoMessage.setText(rimoGetTire.sdoMessage.toString());
  }

  public void updateRateColor(RimoGetTire rimoGetTire) {
    Scalar scalar = rimoGetTire.getAngularRate_Y();
    Tensor vector = ColorDataGradients.TEMPERATURE_LIGHT.apply(RATE_RANGE.rescale(scalar));
    Color color = ColorFormat.toColor(vector);
    jTF_actual_speed.setBackground(color);
  }
}
