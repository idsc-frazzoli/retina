// code by jph
package ch.ethz.idsc.retina.util.data;

import java.io.File;
import java.io.FileNotFoundException;
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
import ch.ethz.idsc.tensor.io.Import;

/** manages configurable parameters by introspection of a given instance
 * 
 * values of non-final, non-static, non-transient but public members of type
 * {@link Tensor}, {@link Scalar}, {@link String}, {@link Boolean}
 * are stored in, and retrieved from files in the {@link Properties} format */
public class TensorProperties {
  private static final int MASK_FILTER = Modifier.PUBLIC;
  private static final int MASK_TESTED = //
      Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT | MASK_FILTER;

  /** Hint: function is used to create a GUI to edit the tracked fields
   * <pre>
   * for (Field field : object.getClass().getFields())
   * if (TensorProperties.isTracked(field))
   * ...
   * </pre>
   * 
   * @param field
   * @return */
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

  /** @param field
   * @param string
   * @return object with content parsed from given string */
  public static Object parse(Field field, String string) {
    return parse(field.getType(), string);
  }

  /* package */ static Object parse(Class<?> type, String string) {
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

  /***************************************************/
  public static TensorProperties wrap(Object object) {
    return new TensorProperties(object);
  }

  private final Object object;

  private TensorProperties(Object object) {
    this.object = object;
  }

  /** @param object
   * @return properties with fields of given object as keys mapping to values as string expression */
  /* package */ Properties get() {
    return get(new Properties());
  }

  private Properties get(Properties properties) {
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
   * @return given object
   * @throws Exception if either of the input parameters is null */
  public void set(Properties properties) {
    for (Field field : object.getClass().getFields())
      if (isTracked(field)) {
        String string = properties.getProperty(field.getName());
        if (Objects.nonNull(string))
          try {
            field.set(object, parse(field, string));
          } catch (Exception exception) {
            exception.printStackTrace();
          }
      }
    // return null;
  }

  /***************************************************/
  /** values defined in properties file are assigned to fields of given object
   * 
   * @param file properties
   * @param object
   * @return object with fields updated from properties file
   * @throws IOException
   * @throws FileNotFoundException */
  @SuppressWarnings("unchecked")
  public <T> T load(File file) throws FileNotFoundException, IOException {
    set(Import.properties(file));
    return (T) object;
  }

  /** store tracked fields of given object in file
   * 
   * @param file
   * @param object
   * @throws IOException */
  public void save(File file) throws IOException {
    Files.write(file.toPath(), (Iterable<String>) strings()::iterator);
  }

  /* package */ List<String> strings() {
    List<String> list = new LinkedList<>();
    for (Field field : object.getClass().getFields())
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

  @SuppressWarnings("unchecked")
  public <T> T tryLoad(File file) {
    try {
      return load(file);
    } catch (Exception exception) {
      // ---
    }
    return (T) object;
  }
}
