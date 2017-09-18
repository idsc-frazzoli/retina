// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JTextField;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;

public class RimoGetFields {
  JTextField jTF_status_word; // 2
  JTextField jTF_actual_speed; // 4
  JTextField jTF_rms_motor_current; // 6
  JTextField jTF_dc_bus_voltage; // 8
  JTextField jTF_error_code; // 10
  JTextField jTF_temperature_motor; // 14
  JTextField jTF_temperature_heatsink; // 16
  // ---

  public void updateText(RimoGetEvent rimoGetEvent) {
    jTF_status_word.setText(String.format("%04X", rimoGetEvent.status_word));
    jTF_actual_speed.setText("" + rimoGetEvent.actual_speed);
    jTF_rms_motor_current.setText("" + rimoGetEvent.rms_motor_current);
    jTF_dc_bus_voltage.setText("" + rimoGetEvent.getBusVoltage() + "[V]");
    jTF_error_code.setText("" + rimoGetEvent.error_code);
    jTF_temperature_motor.setText("" + rimoGetEvent.temperature_motor);
    jTF_temperature_heatsink.setText("" + rimoGetEvent.temperature_heatsink);
  }
}
