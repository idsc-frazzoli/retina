// code by jph
package ch.ethz.idsc.retina.sys;

import java.io.File;

import ch.ethz.idsc.retina.util.data.TensorProperties;

/** user specific customization
 * for instance location of windows */
public enum AppCustomization {
  ;
  private static File file(Class<?> cls) {
    return file(cls.getSimpleName() + ".properties");
  }

  public static File file(String filename) {
    File dir1 = new File("resources", "custom");
    dir1.mkdir();
    File dir2 = new File(dir1, System.getProperty("user.name"));
    dir2.mkdir();
    return new File(dir2, filename);
  }

  /** @param cls
   * @param object
   * @return */
  public static <T> T load(Class<?> cls, T object) {
    return TensorProperties.wrap(object).tryLoad(file(cls));
  }

  /** @param cls
   * @param object */
  public static void save(Class<?> cls, Object object) {
    TensorProperties.wrap(object).trySave(file(cls));
  }
}
