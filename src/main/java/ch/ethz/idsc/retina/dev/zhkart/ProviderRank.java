// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

public enum ProviderRank {
  GODMODE, //
  EMERGENCY, //
  /** for instance when controlling with joystick */
  MANUAL, //
  /** for instance when linmot break calibration */
  CALIBRATION, //
  /** for instance when testing actuators in gui */
  TESTING, //
  QUICKCHECK, //
  AUTONOMOUS, //
  /** if no prior controls have been issued the fallback option is used */
  FALLBACK, //
  ;
}
