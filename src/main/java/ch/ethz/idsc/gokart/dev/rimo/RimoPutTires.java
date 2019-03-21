// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

public enum RimoPutTires {
  ;
  /** the datasheet bounds the speed between -8000 and 8000
   * according to tests on the bench the max effective speed is ~6300 */
  public static final short MIN_SPEED = -6500;
  public static final short MAX_SPEED = +6500;
  /** the torque bounds are taken from the datasheet
   * the unit of the torque is in ARMS, i.e. ampere average root-mean square */
  public static final short MIN_TORQUE = -2317;
  public static final short MAX_TORQUE = +2316;

  public static boolean isTorqueValid(int value) {
    return -MAX_TORQUE <= value && value <= MAX_TORQUE;
  }
}
