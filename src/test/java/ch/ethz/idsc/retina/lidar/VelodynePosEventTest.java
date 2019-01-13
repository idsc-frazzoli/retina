// code by jph
package ch.ethz.idsc.retina.lidar;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class VelodynePosEventTest extends TestCase {
  public void testSimple() {
    String nmea = "$GPRMC,155524,A,4724.3266,N,00837.8624,E,002.0,172.1,131217,001.8,E,A*10";
    VelodynePosEvent vpe = new VelodynePosEvent(123, nmea);
    assertEquals(vpe.gpsX(), Quantity.of(8.378624, "deg"));
    assertEquals(vpe.gpsY(), Quantity.of(4724.3266 * 1E-2, "deg"));
    assertEquals(vpe.dateStamp(), "131217");
    assertEquals(vpe.speed(), Quantity.of(2, "knots"));
    assertEquals(vpe.course(), Quantity.of(172.1, "deg"));
  }

  public void testValid() {
    String nmea = "$GPRMC,142802,A,4724.3445,N,00837.8776,E,000.0,111.4,080118,001.8,E,A*1B";
    VelodynePosEvent vpe = new VelodynePosEvent(123, nmea);
    assertTrue(vpe.isValid());
    assertEquals(vpe.timeStamp(), "142802");
    assertEquals(vpe.dateStamp(), "080118");
  }

  public void testInvalid() {
    String nmea = "$GPRMC,142802,V,4724.3445,N,00837.8776,E,000.0,111.4,080118,001.8,E,A*1B";
    VelodynePosEvent vpe = new VelodynePosEvent(123, nmea);
    assertFalse(vpe.isValid());
    assertEquals(vpe.speed(), Quantity.of(0, "knots"));
    assertEquals(vpe.course(), Quantity.of(111.4, "deg"));
  }
}
