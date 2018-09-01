// code by jph
package ch.ethz.idsc.retina.util.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

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

// TODO JPH more tests!
public class TensorPropertiesTest extends TestCase {
  public void testListSize1() throws Exception {
    ParamContainer ori = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(ori);
    ori.string = "some string, no new line please";
    ori.maxTor = Scalars.fromString("3.13[m*s^2]");
    List<String> list = tensorProperties.strings();
    assertEquals(list.size(), 2);
  }

  public void testListSize2() throws Exception {
    ParamContainer ori = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(ori);
    ori.shape = Tensors.fromString("{1,2,3}");
    ori.abc = RealScalar.ONE;
    ori.maxTor = Scalars.fromString("3.13[m*s^2]");
    List<String> list = tensorProperties.strings();
    assertEquals(list.size(), 3);
  }

  public void testBoolean() throws Exception {
    ParamContainer ori = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(ori);
    ori.status = true;
    assertEquals(tensorProperties.strings().size(), 1);
    Properties properties = tensorProperties.get();
    assertEquals(properties.getProperty("status"), "true");
    properties.setProperty("status", "corrupt");
    tensorProperties.set(properties);
    assertNull(ori.status);
    assertEquals(tensorProperties.strings().size(), 0);
    // ---
    properties.setProperty("status", "true");
    tensorProperties.set(properties);
    assertTrue(ori.status);
    assertEquals(tensorProperties.strings().size(), 1);
    // ---
    properties.setProperty("status", "false");
    tensorProperties.set(properties);
    assertFalse(ori.status);
    assertEquals(tensorProperties.strings().size(), 1);
  }

  public void testLoadFail() {
    // try {
    // TensorProperties.set(null, new ParamContainer());
    // assertTrue(false);
    // } catch (Exception exception) {
    // // ---
    // }
    // try {
    // TensorProperties.set(new Properties(), null);
    // assertTrue(false);
    // } catch (Exception exception) {
    // // ---
    // }
  }

  public void testStore() throws Exception {
    ParamContainer ori = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(ori);
    ori.string = "some string, no new line please";
    assertEquals(tensorProperties.strings().size(), 1);
    ori.maxTor = Scalars.fromString("3.13[m*s^2]");
    ori.shape = Tensors.fromString("{1,2,3}");
    assertEquals(tensorProperties.strings().size(), 3);
    ori.abc = RealScalar.ONE;
    assertEquals(tensorProperties.strings().size(), 4);
    Properties properties = tensorProperties.get();
    {
      ParamContainer pc = new ParamContainer();
      TensorProperties tensorProperties2 = TensorProperties.wrap(pc);
      tensorProperties2.set(properties);
      assertEquals(ori.string, pc.string);
      assertEquals(ori.maxTor, pc.maxTor);
      assertEquals(ori.shape, pc.shape);
      assertEquals(ori.abc, pc.abc);
    }
    {
      ParamContainer pc = new ParamContainer();
      TensorProperties tensorProperties2 = TensorProperties.wrap(pc);
      tensorProperties2.set(properties);
      assertEquals(ori.string, pc.string);
      assertEquals(ori.maxTor, pc.maxTor);
      assertEquals(ori.shape, pc.shape);
      assertEquals(ori.abc, pc.abc);
    }
  }

  public void testInsert() {
    Properties properties = new Properties();
    properties.setProperty("maxTor", "123[m]");
    properties.setProperty("shape", "{3   [s*kg],8*I}");
    ParamContainer pc = new ParamContainer();
    Field[] fields = ParamContainer.class.getFields();
    for (Field field : fields)
      if (!Modifier.isStatic(field.getModifiers()))
        try {
          // System.out.println(field.getName());
          Class<?> cls = field.getType();
          final String string = properties.getProperty(field.getName());
          if (Objects.nonNull(string)) {
            if (cls.equals(Tensor.class))
              field.set(pc, Tensors.fromString(string));
            else //
            if (cls.equals(Scalar.class))
              field.set(pc, Scalars.fromString(string));
            else//
            if (cls.equals(String.class))
              field.set(pc, string);
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    assertTrue(pc.maxTor instanceof Quantity);
    assertFalse(pc.shape.stream().anyMatch(scalar -> scalar instanceof StringScalar));
    assertEquals(pc.shape.length(), 2);
  }

  public void testUsername() {
    String name = System.getProperty("user.name");
    assertFalse(name.isEmpty());
  }

  public void testManifest() throws IOException {
    File file = UserHome.file("TensorProperties_testfile.properties");
    assertFalse(file.exists());
    TensorProperties tensorProperties = TensorProperties.wrap(SensorsConfig.GLOBAL);
    tensorProperties.save(file);
    assertTrue(file.exists());
    SensorsConfig sc = new SensorsConfig();
    TensorProperties.wrap(sc).load(file);
    assertEquals(sc.urg04lx, SensorsConfig.GLOBAL.urg04lx);
    assertEquals(sc.vlp16Height, SensorsConfig.GLOBAL.vlp16Height);
    file.delete();
  }

  public void testRetrieveFail() {
    TensorProperties tensorProperties = TensorProperties.wrap(new GokartLogConfig());
    try {
      tensorProperties.load(new File("asd"));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
