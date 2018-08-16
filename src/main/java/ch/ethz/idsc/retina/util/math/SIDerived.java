// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.qty.Unit;

/** https://en.wikipedia.org/wiki/SI_derived_unit */
public enum SIDerived {
  ;
  public static final Unit RADIAN = Unit.of("rad");
  public static final Unit RADIAN_PER_SECOND = Unit.of("rad*s^-1");
  public static final Unit METER_PER_RADIAN = Unit.of("m*rad^-1");
}
