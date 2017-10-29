// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.io.File;

import ch.ethz.idsc.retina.util.data.TensorProperties;

public enum GokartResources {
  ;
  private static File file(Object object) {
    return new File("resources/properties", object.getClass().getSimpleName() + ".properties");
  }

  public static <T> T load(T object) {
    return TensorProperties.retrieve(file(object), object);
  }

  public static void save(Object object) {
    try {
      TensorProperties.manifest(file(object), object);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
