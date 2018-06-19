// code by jph
package ch.ethz.idsc.retina.util.data;

/** strict parser of boolean value that only returns non-null
 * for input of either verbatim "false", or "true" */
/* package */ enum BooleanParser {
  ;
  /** "true" */
  private static final String TRUE = Boolean.TRUE.toString();
  /** "false" */
  private static final String FALSE = Boolean.FALSE.toString();

  /** stricter function than {@link Boolean#parseBoolean(String)}
   * 
   * @param string
   * @return null if string does not equal "true" or "false" */
  static Boolean orNull(String string) {
    if (string.equals(FALSE))
      return false;
    return string.equals(TRUE) ? true : null;
  }
}
