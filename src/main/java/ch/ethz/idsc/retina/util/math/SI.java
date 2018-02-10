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
  public static final Unit VELOCITY = Unit.of("m*s^-1");
  public static final Unit ACCELERATION = Unit.of("m*s^-2");
  public static final Unit ANGULAR_RATE = Unit.of("s^-1");
  // ---
  public static final Unit DEGREE_ANGLE = Unit.of("deg");
  public static final Unit DEGREE_CELSIUS = Unit.of("degC");
}
