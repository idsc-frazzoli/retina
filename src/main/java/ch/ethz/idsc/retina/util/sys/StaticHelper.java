// code by niam jen wei
package ch.ethz.idsc.retina.util.sys;

/** string manipulation functions */
/* package */ enum StaticHelper {
  ;
  public static String putSpaceBefCaps(String string) {
    return string.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
  }
}