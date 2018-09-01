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
}
