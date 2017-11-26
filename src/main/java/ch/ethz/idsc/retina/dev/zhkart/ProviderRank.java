// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import ch.ethz.idsc.retina.sys.SafetyCritical;

/** rank of a {@link PutProvider} in order of priority
 * 
 * for instance, messages for calibration have higher precedence than messages for testing */
@SafetyCritical
public enum ProviderRank {
  /** not used */
  GODMODE, //
  /** for instance when the battery is low, bumper has contact, flat tire, ... */
  EMERGENCY, //
  /** for instance when
   * 1) linmot break calibration, or
   * 2) steer calibration */
  CALIBRATION, //
  /** for instance when controlling with joystick */
  MANUAL, //
  /** for instance when testing actuators in gui */
  TESTING, //
  /** for instance when lidar detects approaching obstacle
   * that is too fast to be considered by the path planner
   * safety control may override autonomous logic */
  SAFETY, //
  /** path planner */
  AUTONOMOUS, //
  /** if no prior controls have been issued the fallback option is used
   * all systems idle, hand-brake mode */
  FALLBACK, //
  ;
}
