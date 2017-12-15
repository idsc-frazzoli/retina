// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class VelodynePosEventTest extends TestCase {
  public void testSimple() {
    String nmea = //
        "$GPRMC,155524,A,4724.3266,N,00837.8624,E,002.0,172.1,131217,001.8,E,A*10";
    VelodynePosEvent vpe = new VelodynePosEvent(123, nmea);
    assertEquals(vpe.gpsX(), Quantity.of(8.378624, "deg"));
    assertEquals(vpe.gpsY(), Quantity.of(4724.3266 * 1E-2, "deg"));
  }
}
