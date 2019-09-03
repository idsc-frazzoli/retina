// code by jph
package ch.ethz.idsc.gokart.gui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TensorProperties;
import ch.ethz.idsc.tensor.qty.KnownUnitQ;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.ref.FieldIntegerQ;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;
import ch.ethz.idsc.tensor.ref.TensorReflection;
import ch.ethz.idsc.tensor.sca.Clip;
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

  public void testFieldIntegerQ() {
    int checked = 0;
    for (Object object : ParametersHelper.OBJECTS) {
      TensorProperties tensorProperties = TensorProperties.wrap(object);
      List<Field> list = tensorProperties.fields().collect(Collectors.toList());
      for (Field field : list) {
        FieldIntegerQ fieldIntegerQ = field.getAnnotation(FieldIntegerQ.class);
        if (Objects.nonNull(fieldIntegerQ))
          try {
            Scalar scalar = (Scalar) field.get(object); // may throw Exception
            IntegerQ.require(scalar);
            ++checked;
          } catch (Exception exception) {
            System.err.println(field);
            fail();
          }
      }
    }
    assertTrue(0 < checked);
  }

  private static final KnownUnitQ KNOWN_UNIT_Q = KnownUnitQ.in(GokartUnitSystem.INSTANCE.unitSystem);

  public void testUnitSystem() throws IllegalArgumentException, IllegalAccessException {
    for (Object object : ParametersHelper.OBJECTS) {
      List<Field> list = Stream.of(object.getClass().getFields()).collect(Collectors.toList());
      for (Field field : list) {
        Class<?> cls = field.getType();
        if (cls.equals(Tensor.class)) {
          Tensor tensor = (Tensor) field.get(object); // may throw Exception
          tensor.flatten(-1) //
              .map(Scalar.class::cast) //
              .map(QuantityUnit::of) //
              .forEach(KNOWN_UNIT_Q::require);
        }
        if (cls.equals(Scalar.class)) {
          Scalar scalar = (Scalar) field.get(object); // may throw Exception
          KNOWN_UNIT_Q.require(QuantityUnit.of(scalar));
        }
      }
    }
  }

  public void testFieldSubdivideClip() throws IllegalArgumentException, IllegalAccessException {
    for (Object object : ParametersHelper.OBJECTS) {
      TensorProperties tensorProperties = TensorProperties.wrap(object);
      List<Field> list = tensorProperties.fields().collect(Collectors.toList());
      for (Field field : list) {
        Class<?> cls = field.getType();
        if (cls.equals(Scalar.class)) {
          FieldSubdivide fieldSubdivide = field.getAnnotation(FieldSubdivide.class);
          if (Objects.nonNull(fieldSubdivide)) {
            Clip clip = TensorReflection.clip(fieldSubdivide);
            Scalar scalar = (Scalar) field.get(object); // may throw Exception
            clip.requireInside(scalar);
          }
        }
      }
    }
  }
}
