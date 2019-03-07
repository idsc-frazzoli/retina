// code by jph
package ch.ethz.idsc.gokart.dev.steer;

/* package */ enum SteerGetStatus {
  /** value briefly after power on */
  STARTING, //
  /** value as non-enabled */
  DISABLED, //
  /** value required for normal operation */
  OPERATIONAL, //
  ;
  /** @param value
   * @return true if value corresponds to status of instance */
  public boolean of(float value) {
    return ordinal() == value;
  }

  public float value() {
    return ordinal();
  }
}
