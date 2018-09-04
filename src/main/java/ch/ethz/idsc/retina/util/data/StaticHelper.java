// code by jph
package ch.ethz.idsc.retina.util.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum StaticHelper {
  ;
  private static final int MASK_FILTER = Modifier.PUBLIC;
  private static final int MASK_TESTED = //
      Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT | MASK_FILTER;

  /** @param field
   * @return if field is managed by {@link TensorProperties} */
  /* package */ static boolean isTracked(Field field) {
    if ((field.getModifiers() & MASK_TESTED) == MASK_FILTER) {
      Class<?> type = field.getType();
      return type.equals(Tensor.class) //
          || type.equals(Scalar.class) //
          || type.equals(String.class) //
          || type.equals(Boolean.class);
    }
    return false;
  }

  /** @param cls
   * @param string
   * @return new instance of class that was constructed from given string */
  /* package */ static Object parse(Class<?> cls, String string) {
    if (cls.equals(Tensor.class))
      return Tensors.fromString(string);
    if (cls.equals(Scalar.class))
      return Scalars.fromString(string);
    if (cls.equals(String.class))
      return string;
    if (cls.equals(Boolean.class))
      return BooleanParser.orNull(string);
    return null;
  }
}
