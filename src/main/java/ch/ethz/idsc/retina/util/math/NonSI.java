// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.qty.Unit;

/** conversion to non-SI magnitude may be necessary
 * when interfacing with 3rd party code that requires input along that scale */
public enum NonSI {
  ;
  public static final Unit MILLI_SECOND = Unit.of("ms");
  public static final Unit MICRO_SECOND = Unit.of("us");
  // ---
  public static final Unit DEGREE_ANGLE = Unit.of("deg");
  // ---
  public static final Unit DEGREE_CELSIUS = Unit.of("degC");
}
