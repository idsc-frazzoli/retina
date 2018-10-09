// code by jph
package ch.ethz.idsc.gokart.gui;

import java.lang.reflect.InvocationTargetException;

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
}
