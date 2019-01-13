// code by jph
package ch.ethz.idsc.retina.util.sys;

import java.io.File;

import ch.ethz.idsc.tensor.io.TensorProperties;
import ch.ethz.idsc.tensor.io.UserName;

/** user specific customization encoded in ASCII files
 * managed by {@link TensorProperties}.
 * 
 * Example: store location of windows */
public enum AppCustomization {
  ;
  private static File file(Class<?> cls) {
    return file(cls.getSimpleName() + ".properties");
  }

  /** @param filename
   * @return file of the form "resources/custom/username/filename" */
  public static File file(String filename) {
    File dir1 = new File("resources", "custom");
    dir1.mkdir();
    File dir2 = new File(dir1, UserName.get());
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
