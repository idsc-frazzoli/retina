// code by jph
package ch.ethz.idsc.retina.util.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

enum StaticHelper {
  ;
  /** "true" */
  private static final String TRUE = Boolean.TRUE.toString();
  /** "false" */
  private static final String FALSE = Boolean.FALSE.toString();

  /** stricter function than {@link Boolean#parseBoolean(String)}
   * 
   * @param string
   * @return null if string does not equal "true" or "false" */
  static Boolean booleanOrNull(String string) {
    if (string.equals(FALSE))
      return false;
    return string.equals(TRUE) ? true : null;
  }

  /** @param string
   * @return imported properties, or null if resource could not be loaded */
  static Properties load(File file) {
    try (InputStream inputStream = new FileInputStream(file)) {
      Properties properties = new Properties();
      properties.load(inputStream);
      return properties;
    } catch (Exception exception) {
      // ---
    }
    return null;
  }
}
