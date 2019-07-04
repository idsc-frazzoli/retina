// code by jph
package ch.ethz.idsc.gokart.gui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.io.TensorProperties;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;
import ch.ethz.idsc.tensor.ref.TensorReflection;
import junit.framework.TestCase;

public class ParametersHelperTest extends TestCase {
  public void testSimple() throws //
  InstantiationException, //
      IllegalAccessException, //
      IllegalArgumentException, //
      InvocationTargetException, //
      NoSuchMethodException, //
      SecurityException {
    for (Object object : ParametersHelper.OBJECTS)
      object.getClass().getDeclaredConstructor().newInstance();
  }

  public void testFieldSubdivide() {
    for (Object object : ParametersHelper.OBJECTS) {
      TensorProperties tensorProperties = TensorProperties.wrap(object);
      List<Field> list = tensorProperties.fields().collect(Collectors.toList());
      for (Field field : list) {
        FieldSubdivide fieldSubdivide = field.getAnnotation(FieldSubdivide.class);
        if (Objects.nonNull(fieldSubdivide))
          try {
            TensorReflection.strict(fieldSubdivide);
          } catch (Exception exception) {
            System.err.println(object.getClass().getName());
            System.err.println(field);
            fail();
          }
      }
    }
  }
}
