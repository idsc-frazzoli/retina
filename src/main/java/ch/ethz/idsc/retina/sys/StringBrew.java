// code by niam jen wei
package ch.ethz.idsc.retina.sys;

/** string manipulation functions */
/* package */ enum StringBrew {
  ;
  public static String putSpaceBefCaps(String string) {
    return string.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
  }
}