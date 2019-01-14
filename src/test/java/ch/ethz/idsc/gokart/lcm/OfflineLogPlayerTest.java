// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.lidar.VelodynePosListener;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.gps.WGS84toCH1903LV03Plus;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class OfflineLogPlayerTest extends TestCase {
  public void testFromFile() throws IOException {
    Clip clipX = Clip.function(Quantity.of(8.3, "deg"), Quantity.of(8.4, "deg"));
    Clip clipY = Clip.function(Quantity.of(47.2, "deg"), Quantity.of(47.3, "deg"));
    Clip clipMeterX = Clip.function(Quantity.of(2671166, "m"), Quantity.of(2671196, "m"));
    Clip clipMeterY = Clip.function(Quantity.of(1232902, "m"), Quantity.of(1232942, "m"));
    File file = new File("src/test/resources/localization", "vlp16.center.pos.lcm");
    assertTrue(file.isFile());
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    VelodynePosListener velodynePosListener = new VelodynePosListener() {
      @Override
      public void velodynePos(VelodynePosEvent velodynePosEvent) {
        assertTrue(velodynePosEvent.nmea().startsWith("$GPRMC"));
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
      public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
        if (channel.equals("vlp16.center.pos")) {
          velodyneDecoder.positioning(byteBuffer);
        }
      }
    };
    OfflineLogPlayer.process(file, offlineLogListener);
  }
}
