// code by jph
package ch.ethz.idsc.retina.util.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;

/** manages configurable parameters by introspection of a given instance
 * 
 * values of non-final, non-static, non-transient but public members of type
 * {@link Tensor}, {@link Scalar}, {@link String}, {@link Boolean}
 * are stored in, and retrieved from files in the {@link Properties} format */
public class TensorProperties {
  /** @param object
   * @return */
  public static TensorProperties wrap(Object object) {
    return new TensorProperties(object);
  }

  /** @param field
   * @param string
   * @return object with content parsed from given string */
  public static Object parse(Field field, String string) {
    return StaticHelper.parse(field.getType(), string);
  }

  /***************************************************/
  private final Object object;

  private TensorProperties(Object object) {
    this.object = Objects.requireNonNull(object);
  }

  /** @return stream of tracked fields of given object
   * in the order in which they appear top to bottom in the class */
  public Stream<Field> fields() {
    return Stream.of(object.getClass().getFields()) //
        .filter(StaticHelper::isTracked);
  }

  /** @param properties
   * @param object with fields to be assigned according to given properties
   * @return given object
   * @throws Exception if properties is null */
  @SuppressWarnings("unchecked")
  public <T> T set(Properties properties) {
    fields().forEach(field -> {
      String string = properties.getProperty(field.getName());
      if (Objects.nonNull(string))
        try {
          field.set(object, parse(field, string));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    });
    return (T) object;
  }

  /** @param object
   * @return properties with fields of given object as keys mapping to values as string expression */
  /* package */ Properties get() {
    Properties properties = new Properties();
    consume(properties::setProperty);
    return properties;
  }

  /***************************************************/
  /** values defined in properties file are assigned to fields of given object
   * 
   * @param file properties
   * @param object
   * @return object with fields updated from properties file
   * @throws IOException
   * @throws FileNotFoundException */
  public void load(File file) throws FileNotFoundException, IOException {
    set(Import.properties(file));
  }

  @SuppressWarnings("unchecked")
  public <T> T tryLoad(File file) {
    try {
      load(file);
    } catch (Exception exception) {
      // ---
    }
    return (T) object;
  }

  /** store tracked fields of given object in given file
   * 
   * @param file properties
   * @param object
   * @throws IOException */
  public void save(File file) throws IOException {
    Files.write(file.toPath(), (Iterable<String>) strings()::iterator);
  }

  /** @param file */
  public void trySave(File file) {
    try {
      save(file);
    } catch (Exception exception) {
      // ---
    }
  }

  /* package */ List<String> strings() {
    List<String> list = new LinkedList<>();
    consume((field, value) -> list.add(field + "=" + value));
    return list;
  }

  // helper function
  private void consume(BiConsumer<String, String> biConsumer) {
    fields().forEach(field -> {
      try {
        Object value = field.get(object); // may throw Exception
        if (Objects.nonNull(value))
          biConsumer.accept(field.getName(), value.toString());
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    });
  }
}
