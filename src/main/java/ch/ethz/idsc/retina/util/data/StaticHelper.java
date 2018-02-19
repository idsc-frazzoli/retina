// code by jph
package ch.ethz.idsc.retina.util.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

enum StaticHelper {
  ;
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
