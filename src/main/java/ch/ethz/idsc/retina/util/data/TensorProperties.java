// code by jph
package ch.ethz.idsc.retina.util.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** manages configurable parameters by introspection of a given instance
 * 
 * values of members of type {@link Tensor} or {@link Scalar} are stored in
 * and retrieved from files in the {@link Properties} format */
public enum TensorProperties {
  ;
  /** @param object
   * @return properties with fields of given object as keys mapping to values as string expression */
  public static Properties extract(Object object) {
    return extract(object, new Properties());
  }

  private static Properties extract(Object object, Properties properties) {
    for (Field field : object.getClass().getFields())
      if (isTracked(field))
        try {
          Object value = field.get(object);
          if (Objects.nonNull(value))
            properties.setProperty(field.getName(), value.toString());
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    return properties;
  }

  public static <T> T insert(Properties properties, T object) {
    if (Objects.isNull(properties))
      return object;
    for (Field field : object.getClass().getFields())
      if (isTracked(field))
        try {
          Class<?> type = field.getType();
          final String string = properties.getProperty(field.getName());
          if (Objects.nonNull(string)) {
            if (type.equals(Tensor.class))
              field.set(object, Tensors.fromString(string));
            else //
            if (type.equals(Scalar.class))
              field.set(object, Scalars.fromString(string));
            else //
            if (type.equals(String.class))
              field.set(object, string);
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    return object;
  }

  public static <T> T newInstance(Properties properties, Class<T> cls) //
      throws InstantiationException, IllegalAccessException {
    return insert(properties, cls.newInstance());
  }

  public static boolean isTracked(Field field) {
    int mod = field.getModifiers();
    if (!Modifier.isFinal(mod) && !Modifier.isStatic(mod) && Modifier.isPublic(mod)) {
      Class<?> type = field.getType();
      return type.equals(Tensor.class) || type.equals(Scalar.class) || type.equals(String.class);
    }
    return false;
  }

  /** values defined in properties file are assigned to fields of given object
   * 
   * @param file properties
   * @param object
   * @return object */
  public static <T> T retrieve(File file, T object) {
    return insert(StaticHelper.load(file), object);
  }

  public static void manifest(File file, Object object) throws IOException {
    Files.write(file.toPath(), (Iterable<String>) strings(object)::iterator);
  }

  private static List<String> strings(Object object) {
    List<String> list = new LinkedList<>();
    Field[] fields = object.getClass().getFields();
    for (Field field : fields)
      if (isTracked(field))
        try {
          Object value = field.get(object);
          if (Objects.nonNull(value))
            list.add(field.getName() + "=" + value.toString());
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    return list;
  }
}
