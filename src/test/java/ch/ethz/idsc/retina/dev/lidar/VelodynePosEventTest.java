// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.gps.WGS84toCH1903LV03Plus;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;
import lcm.logging.Log.Event;

public class VelodynePosEventTest extends TestCase {
  public void testSimple() {
    String nmea = //
        "$GPRMC,155524,A,4724.3266,N,00837.8624,E,002.0,172.1,131217,001.8,E,A*10";
    VelodynePosEvent vpe = new VelodynePosEvent(123, nmea);
    assertEquals(vpe.gpsX(), Quantity.of(8.378624, "deg"));
    assertEquals(vpe.gpsY(), Quantity.of(4724.3266 * 1E-2, "deg"));
  }

  public void testFromFile() throws IOException {
    Clip clipX = Clip.function(Quantity.of(8.3, "deg"), Quantity.of(8.4, "deg"));
    Clip clipY = Clip.function(Quantity.of(47.2, "deg"), Quantity.of(47.3, "deg"));
    Clip clipMeterX = Clip.function(Quantity.of(2671166, "m"), Quantity.of(2671196, "m"));
    Clip clipMeterY = Clip.function(Quantity.of(1232902, "m"), Quantity.of(1232942, "m"));
    File file = new File("src/test/resources/localization", "vlp16.center.pos.lcm");
    assertTrue(file.isFile());
    Vlp16Decoder velodyneDecoder = new Vlp16Decoder();
    VelodynePosListener velodynePosListener = new VelodynePosListener() {
      @Override
      public void velodynePos(VelodynePosEvent velodynePosEvent) {
        assertTrue(velodynePosEvent.nmea().startsWith("$GPRMC"));
        // System.out.println(velodynePosEvent.nmea());
        Scalar degX = velodynePosEvent.gpsX();
        assertTrue(clipX.isInside(degX));
        Scalar degY = velodynePosEvent.gpsY();
        assertTrue(clipY.isInside(degY));
        Tensor metric = WGS84toCH1903LV03Plus.transform(degX, degY);
        assertTrue(clipMeterX.isInside(metric.Get(0)));
        assertTrue(clipMeterY.isInside(metric.Get(1)));
      }
    };
    velodyneDecoder.addPosListener(velodynePosListener);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, Event event, ByteBuffer byteBuffer) {
        // System.out.println(time.number().doubleValue() + " " + event.channel);
        if (event.channel.equals("vlp16.center.pos")) {
          velodyneDecoder.positioning(byteBuffer);
        }
      }
    };
    OfflineLogPlayer.process(file, offlineLogListener);
  }
}
