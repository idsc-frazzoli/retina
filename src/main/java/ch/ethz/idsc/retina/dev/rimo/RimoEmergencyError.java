// code by jph
package ch.ethz.idsc.retina.dev.rimo;

/** EA_12_001_e_06_13_ACD_Controller_Setting Up V1_5
 * page 27-28, Table 2 */
public enum RimoEmergencyError {
  /** Current, Voltage and Temperature Errors */
  AC_CURRENT_OVER_CURRENT(0x2310), //
  AC_CURRENT_SHORT_CIRCUIT(0x2340), //
  DC_BUS_CHARGING_TIMEOUT(0x3120), //
  DC_BUS_HIGH_SOFTWARE(0x3211), //
  DC_BUS_HIGH_HARDWARE(0x3212), //
  DC_BUS_LOW_SOFTWARE(0x3221), //
  MOTOR_TEMPERATURE_HIGH(0x4210), // fire in the hole
  HEATSINK_TEMPERATURE_HIGH(0x4310), // fire in the hole
  /** Device Hardware Errors */
  _15V_SUPPLY_LOW_VOLTAGE(0x5111), //
  _5V_SUPPLY_LOW_VOLTAGE(0x5113), //
  CURRENT_SENSOR_OFFSET_CALIBRATION(0x5210), //
  OPEN_DRAIN_OUTPUTS_CURRENT_HIGH(0x5410), //
  /** Product specific Application Errors */
  PRODUCT_APPLICATION_ERROR_0(0x6210), //
  PRODUCT_APPLICATION_ERROR_1(0x6211), //
  PRODUCT_APPLICATION_ERROR_2(0x6212), //
  PRODUCT_APPLICATION_ERROR_3(0x6213), //
  PRODUCT_APPLICATION_ERROR_4(0x6214), //
  /** General application Errors */
  SPEED_SENSOR(0x7310), //
  POSITION_CALIBRATION_TIMEOUT(0x7320), //
  POSITION_CALIBRATION_ERROR(0x7321), //
  POSITION_SENSOR(0x7322), //
  POSITION_REFERENCE_GENERATOR(0x7380), //
  /** Overall safety Errors */
  CURRENT_REGULATOR_SUPERVISION(0x8010), //
  POSITION_REGULATOR_SUPERVISION(0x8020), //
  MOTOR_CABLE_NOT_CONNECTED(0x8030), //
  SPEED_REGULATOR_SUPERVISION(0x8040), //
  INDUCTION_SPEED_ESTIMATION_SUPERVISION(0x8050), //
  /** Device software Errors */
  CAN_TIMEOUT(0x8100), //
  ;
  // ---
  public final short code;

  private RimoEmergencyError(int code) {
    this.code = (short) code;
  }
}
