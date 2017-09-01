// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import junit.framework.TestCase;

public class Urg04lxDeviceTest extends TestCase {
  public void testSimple() {
    try (InputStream inputStream = Urg04lxDeviceTest.class.getResourceAsStream("/urg04lx/urh20170809T163714_crop.txt")) {
      Stream<String> lines = _lines(inputStream);
      List<String> list = lines.collect(Collectors.toList());
      String line = list.get(0);
      Urg04lxEvent event = Urg04lxEvent.fromString(line);
      assertTrue(event.range[event.range.length] == 537);
      assertEquals(event.range.length, Urg04lxDevice.POINTS);
    } catch (Exception exception) {
      // ---
    }
  }

  // helper function
  private static Stream<String> _lines(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream)).lines();
  }
}
