// code by jph
package ch.ethz.idsc.retina.util;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

/** annotation to indicate that a class is not used anymore
 * and should be removed in the next general maintenance. */
@Target(TYPE)
public @interface Obsolete {
  // ---
}
