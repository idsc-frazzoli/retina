// code by niam jen wei
package ch.ethz.idsc.retina.sys;

/** String manipulation functions */
public enum StringBrew {
  ;
  public static String putSpaceBefCaps(String in) {
    return in.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
  }

  public static String removeLastExtension(String str) {
    if (str != null && str.contains("."))
      return str.substring(0, str.lastIndexOf('.'));
    return str;
  }
}