// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.qty.Unit;

public enum SI {
  ;
  public static final Unit ONE = Unit.ONE;
  // ---
  public static final Unit METER = Unit.of("m");
  public static final Unit SECOND = Unit.of("s");
  public static final Unit VOLT = Unit.of("V");
  // ---
  /** per meter is used for instance when specifying max rotation per meter driven */
  public static final Unit PER_METER = Unit.of("m^-1");
  // ---
  public static final Unit VELOCITY = Unit.of("m*s^-1");
  public static final Unit ACCELERATION = Unit.of("m*s^-2");
  public static final Unit PER_SECOND = Unit.of("s^-1");
  public static final Unit ANGULAR_ACCELERATION = Unit.of("s^-2");
}
