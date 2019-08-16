// code by jph
package ch.ethz.idsc.gokart.gui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TensorProperties;
import ch.ethz.idsc.tensor.qty.KnownUnitQ;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.qty.Unit;
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

  private static final KnownUnitQ KNOWN_UNIT_Q = KnownUnitQ.in(GokartUnitSystem.INSTANCE.unitSystem);

  // TODO JPH TENSOR V078 obsolete
  private static void require(Scalar scalar) {
    Unit unit = QuantityUnit.of(scalar);
    if (!KNOWN_UNIT_Q.of(unit)) {
      System.err.println(unit);
      fail();
    }
  }

  public void testUnitSystem() throws IllegalArgumentException, IllegalAccessException {
    for (Object object : ParametersHelper.OBJECTS) {
      List<Field> list = Stream.of(object.getClass().getFields()).collect(Collectors.toList());
      for (Field field : list) {
        Class<?> cls = field.getType();
        if (cls.equals(Tensor.class)) {
          Tensor tensor = (Tensor) field.get(object); // may throw Exception
          tensor.flatten(-1).map(Scalar.class::cast).forEach(ParametersHelperTest::require);
        }
        if (cls.equals(Scalar.class)) {
          Scalar scalar = (Scalar) field.get(object); // may throw Exception
          require(scalar);
        }
      }
    }
  }
}
