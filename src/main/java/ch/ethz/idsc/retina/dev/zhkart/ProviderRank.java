// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

public enum ProviderRank {
  /** not used */
  GODMODE, //
  /** for instance when the battery is low, bumper has contact, flat tire, ... */
  EMERGENCY, //
  /** for instance when linmot break calibration,
   * or steer calibration */
  CALIBRATION, //
  /** for instance when controlling with joystick */
  MANUAL, //
  /** for instance when testing actuators in gui */
  TESTING, //
  /** for instance when lidar detects approaching obstacle
   * that is too fast to be considered by the path planner */
  SAFETY, //
  /** path planner */
  AUTONOMOUS, //
  /** if no prior controls have been issued the fallback option is used
   * all systems idle, hand-brake mode */
  FALLBACK, //
  ;
}
