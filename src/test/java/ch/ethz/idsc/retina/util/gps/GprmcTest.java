// code by jph
package ch.ethz.idsc.retina.util.gps;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GprmcTest extends TestCase {
  public void testSimple() {
    String nmea = "$GPRMC,155524,A,4724.3266,N,00837.8624,E,002.0,172.1,131217,001.8,E,A*10";
    Gprmc gprmc = Gprmc.of(nmea);
    assertEquals(gprmc.gpsX(), Quantity.of(8.378624, "deg"));
    assertEquals(gprmc.gpsY(), Quantity.of(4724.3266 * 1E-2, "deg"));
    assertEquals(gprmc.dateStamp(), "131217");
    assertEquals(gprmc.speed(), Quantity.of(2, "knots"));
    assertEquals(gprmc.course(), Quantity.of(172.1, "deg"));
  }

  public void testValid() {
    String nmea = "$GPRMC,142802,A,4724.3445,N,00837.8776,E,000.0,111.4,080118,001.8,E,A*1B";
    Gprmc gprmc = Gprmc.of(nmea);
    assertTrue(gprmc.isValid());
    assertEquals(gprmc.timestamp(), "142802");
    assertEquals(gprmc.dateStamp(), "080118");
  }

  public void testInvalid() {
    String nmea = "$GPRMC,142802,V,4724.3445,N,00837.8776,E,000.0,111.4,080118,001.8,E,A*1B";
    Gprmc gprmc = Gprmc.of(nmea);
    assertFalse(gprmc.isValid());
    assertEquals(gprmc.speed(), Quantity.of(0, "knots"));
    assertEquals(gprmc.course(), Quantity.of(111.4, "deg"));
  }
}
