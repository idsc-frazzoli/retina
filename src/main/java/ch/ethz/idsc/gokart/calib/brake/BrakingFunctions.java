// code by jph
package ch.ethz.idsc.gokart.calib.brake;

public enum BrakingFunctions {
  ;
  public static final StaticBrakingFunction STATIC = new StaticBrakingFunction();
  public static final SelfCalibratingBrakingFunction CALIBRATING = new SelfCalibratingBrakingFunction();
}
