// code by jph
package ch.ethz.idsc.retina.util.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.offline.api.GokartLogConfig;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class TensorPropertiesTest extends TestCase {
  public void testListSize1() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    paramContainer.string = "some string, no new line please";
    paramContainer.maxTor = Scalars.fromString("3.13[m*s^2]");
    List<String> list = tensorProperties.strings();
    assertEquals(list.size(), 2);
  }

  public void testListSize2() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    paramContainer.shape = Tensors.fromString("{1,2,3}");
    paramContainer.abc = RealScalar.ONE;
    paramContainer.maxTor = Scalars.fromString("3.13[m*s^2]");
    List<String> list = tensorProperties.strings();
    assertEquals(list.size(), 3);
  }

  public void testBoolean() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    paramContainer.status = true;
    assertEquals(tensorProperties.strings().size(), 1);
    Properties properties = tensorProperties.get();
    assertEquals(properties.getProperty("status"), "true");
    properties.setProperty("status", "corrupt");
    tensorProperties.set(properties);
    assertNull(paramContainer.status);
    assertEquals(tensorProperties.strings().size(), 0);
    // ---
    properties.setProperty("status", "true");
    tensorProperties.set(properties);
    assertTrue(paramContainer.status);
    assertEquals(tensorProperties.strings().size(), 1);
    // ---
    properties.setProperty("status", "false");
    tensorProperties.set(properties);
    assertFalse(paramContainer.status);
    assertEquals(tensorProperties.strings().size(), 1);
  }

  public void testStore() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    paramContainer.string = "some string, no new line please";
    assertEquals(tensorProperties.strings().size(), 1);
    paramContainer.maxTor = Scalars.fromString("3.13[m*s^2]");
    paramContainer.shape = Tensors.fromString("{1,2,3}");
    assertEquals(tensorProperties.strings().size(), 3);
    paramContainer.abc = RealScalar.ONE;
    assertEquals(tensorProperties.strings().size(), 4);
    Properties properties = tensorProperties.get();
    {
      ParamContainer pc = new ParamContainer();
      TensorProperties tensorProperties2 = TensorProperties.wrap(pc);
      tensorProperties2.set(properties);
      assertEquals(paramContainer.string, pc.string);
      assertEquals(paramContainer.maxTor, pc.maxTor);
      assertEquals(paramContainer.shape, pc.shape);
      assertEquals(paramContainer.abc, pc.abc);
    }
    {
      ParamContainer pc = new ParamContainer();
      TensorProperties tensorProperties2 = TensorProperties.wrap(pc);
      tensorProperties2.set(properties);
      assertEquals(paramContainer.string, pc.string);
      assertEquals(paramContainer.maxTor, pc.maxTor);
      assertEquals(paramContainer.shape, pc.shape);
      assertEquals(paramContainer.abc, pc.abc);
    }
  }

  public void testInsert() {
    Properties properties = new Properties();
    properties.setProperty("maxTor", "123[m]");
    properties.setProperty("shape", "{3   [s*kg],8*I}");
    ParamContainer paramContainer = new ParamContainer();
    Field[] fields = ParamContainer.class.getFields();
    for (Field field : fields)
      if (!Modifier.isStatic(field.getModifiers()))
        try {
          // System.out.println(field.getName());
          Class<?> cls = field.getType();
          final String string = properties.getProperty(field.getName());
          if (Objects.nonNull(string)) {
            if (cls.equals(Tensor.class))
              field.set(paramContainer, Tensors.fromString(string));
            else //
            if (cls.equals(Scalar.class))
              field.set(paramContainer, Scalars.fromString(string));
            else//
            if (cls.equals(String.class))
              field.set(paramContainer, string);
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    assertTrue(paramContainer.maxTor instanceof Quantity);
    assertFalse(paramContainer.shape.stream().anyMatch(scalar -> scalar instanceof StringScalar));
    assertEquals(paramContainer.shape.length(), 2);
  }

  public void testFields() {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    List<String> list = tensorProperties.fields().map(Field::getName).collect(Collectors.toList());
    assertEquals(list.get(0), "string");
    assertEquals(list.get(1), "maxTor");
    assertEquals(list.get(2), "shape");
    assertEquals(list.get(3), "abc");
    assertEquals(list.get(4), "status");
  }

  public void testManifest() throws IOException {
    File file = UserHome.file("TensorProperties_testfile.properties");
    assertFalse(file.exists());
    TensorProperties tensorProperties = TensorProperties.wrap(SensorsConfig.GLOBAL);
    tensorProperties.save(file);
    assertTrue(file.exists());
    SensorsConfig sensorsConfig = new SensorsConfig();
    TensorProperties.wrap(sensorsConfig).load(file);
    assertEquals(sensorsConfig.urg04lx, SensorsConfig.GLOBAL.urg04lx);
    assertEquals(sensorsConfig.vlp16Height, SensorsConfig.GLOBAL.vlp16Height);
    file.delete();
  }

  public void testLoadFail() {
    TensorProperties tensorProperties = TensorProperties.wrap(new GokartLogConfig());
    try {
      tensorProperties.load(new File("fileDoesNotExist"));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSetFail() {
    TensorProperties tensorProperties = TensorProperties.wrap(new ParamContainer());
    try {
      tensorProperties.set(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testWrapFail() {
    try {
      TensorProperties.wrap(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
