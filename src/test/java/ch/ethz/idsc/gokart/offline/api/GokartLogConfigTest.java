// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.util.Properties;

import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.TensorProperties;
import junit.framework.TestCase;

public class GokartLogConfigTest extends TestCase {
  public void testResources() {
    Properties properties = ResourceData.properties("/offline/20180419T124700_fast/GokartLogConfig.properties");
    GokartLogConfig gokartLogConfig = new GokartLogConfig();
    TensorProperties.wrap(gokartLogConfig).set(properties);
    String driver = gokartLogConfig.driver;
    assertEquals(driver, "abc");
  }
}
