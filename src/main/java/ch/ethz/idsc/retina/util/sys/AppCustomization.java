// code by jph
package ch.ethz.idsc.retina.util.sys;

import java.io.File;

import ch.ethz.idsc.tensor.io.TensorProperties;

/** user specific customization encoded in ASCII files
 * managed by {@link TensorProperties}
 * 
 * Example: store bounds of a window */
public enum AppCustomization {
  ;
  private static final File RESOURCES_CUSTOM = new File("resources", "custom");
  static {
    RESOURCES_CUSTOM.mkdir();
  }

  /** @param filename
   * @return file of the form "resources/custom/application/filename" */
  public static File file(Class<?> application, String filename) {
    File directory = new File(RESOURCES_CUSTOM, application.getSimpleName());
    directory.mkdir();
    return new File(directory, filename);
  }

  /***************************************************/
  /** @param application
   * @param object with custom data, e.g. WindowConfiguration
   * @return */
  public static <T> T load(Class<?> application, T object) {
    return TensorProperties.wrap(object).tryLoad(file(application, object.getClass()));
  }

  /** @param application
   * @param object with custom data, e.g. WindowConfiguration */
  public static void save(Class<?> application, Object object) {
    TensorProperties.wrap(object).trySave(file(application, object.getClass()));
  }

  // helper function
  private static File file(Class<?> application, Class<?> cls) {
    return file(application, cls.getSimpleName() + ".properties");
  }
}
