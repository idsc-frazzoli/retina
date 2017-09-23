// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

public enum ProviderRank {
  GODMODE, //
  EMERGENCY, //
  CALIBRATION, //
  MANUAL, //
  QUICKCHECK, //
  AUTONOMOUS, //
  /** if no prior controls have been issued the fallback option is used */
  FALLBACK, //
  ;
}
