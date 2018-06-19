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
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** manages configurable parameters by introspection of a given instance
 * 
 * values of non-final, non-static, non-transient but public members of type
 * {@link Tensor}, {@link Scalar}, {@link String}, {@link Boolean}
 * are stored in, and retrieved from files in the {@link Properties} format */
public enum TensorProperties {
  ;
  private static final int MASK_FILTER = Modifier.PUBLIC;
  private static final int MASK_TESTED = //
      Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT | MASK_FILTER;
  private static final Collector<CharSequence, ?, String> NEWLINE = Collectors.joining("\n");

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

  /** @param properties
   * @param object with fields to be assigned according to given properties
   * @return given object */
  public static <T> T insert(Properties properties, T object) {
    if (Objects.isNull(properties))
      return object;
    for (Field field : object.getClass().getFields())
      if (isTracked(field))
        try {
          String string = properties.getProperty(field.getName());
          if (Objects.nonNull(string))
            field.set(object, parse(field, string));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    return object;
  }

  /** @param field
   * @param string
   * @return */
  public static Object parse(Field field, String string) {
    Class<?> type = field.getType();
    if (type.equals(Tensor.class))
      return Tensors.fromString(string);
    else //
    if (type.equals(Scalar.class))
      return Scalars.fromString(string);
    else //
    if (type.equals(String.class))
      return string;
    else //
    if (type.equals(Boolean.class))
      return BooleanParser.orNull(string);
    return null;
  }

  public static <T> T newInstance(Properties properties, Class<T> cls) //
      throws InstantiationException, IllegalAccessException {
    return insert(properties, cls.newInstance());
  }

  public static boolean isTracked(Field field) {
    if ((field.getModifiers() & MASK_TESTED) == MASK_FILTER) {
      Class<?> type = field.getType();
      return type.equals(Tensor.class) //
          || type.equals(Scalar.class) //
          || type.equals(String.class) //
          || type.equals(Boolean.class);
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

  /** store tracked fields of given object in file
   * 
   * @param file
   * @param object
   * @throws IOException */
  public static void manifest(File file, Object object) throws IOException {
    Files.write(file.toPath(), (Iterable<String>) strings(object)::iterator);
  }

  /* package for testing */ static List<String> strings(Object object) {
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

  /* package for testing */ static String toString(Object object) {
    return strings(object).stream().collect(NEWLINE);
  }
}
