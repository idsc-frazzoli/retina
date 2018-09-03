// code by jph
package ch.ethz.idsc.retina.util.data;

import java.lang.reflect.Field;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testParseString() {
    Object object = StaticHelper.parse(String.class, "ethz idsc ");
    assertEquals(object, "ethz idsc ");
  }

  public void testParseScalar() {
    Object object = StaticHelper.parse(Scalar.class, " 3/4+8*I[m*s^-2]");
    Scalar scalar = Quantity.of(ComplexScalar.of(RationalScalar.of(3, 4), RealScalar.of(8)), "m*s^-2");
    assertEquals(object, scalar);
  }

  public void testParseBoolean() {
    Object object = StaticHelper.parse(Boolean.class, "true");
    assertEquals(object, Boolean.TRUE);
  }

  public void testIsTracked() {
    Field[] fields = ParamContainer.class.getFields();
    int count = 0;
    for (Field field : fields)
      count += StaticHelper.isTracked(field) ? 1 : 0;
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    int count2 = (int) tensorProperties.fields().count();
    assertEquals(count, count2);
    assertEquals(count, 5);
  }

  public void testUsername() {
    String name = System.getProperty("user.name");
    assertFalse(name.isEmpty());
  }
}
