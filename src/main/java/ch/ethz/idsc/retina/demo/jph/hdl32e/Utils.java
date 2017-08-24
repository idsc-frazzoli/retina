// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePositioningDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRealtimeFiringPacket;
import ch.ethz.idsc.retina.dev.hdl32e.app.Hdl32eFiringFrame;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eAngularFiringCollector;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eRotationProvider;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eSpacialProvider;

public enum Utils {
  ;
  // ---
  static Hdl32ePanoramaFrame createPanoramaDisplay(Hdl32eFiringDecoder hdl32eFiringPacketConsumer) {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    hdl32eFiringPacketConsumer.addListener(hdl32ePanoramaCollector);
    return hdl32ePanoramaFrame;
  }

  public static Hdl32eFiringFrame createFiringFrame( //
      Hdl32eFiringDecoder hdl32eFiringPacketDecoder, //
      Hdl32ePositioningDecoder hdl32ePositioningDecoder) {
    FloatBuffer floatBuffer = FloatBuffer.wrap(new float[2310 * 32 * 3]);
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[2310 * 32]);
    Hdl32eAngularFiringCollector hdl32eAngularFiringCollector = //
        new Hdl32eAngularFiringCollector(floatBuffer, byteBuffer);
    Hdl32eSpacialProvider hdl32eSpacialProvider = new Hdl32eSpacialProvider();
    hdl32eSpacialProvider.addListener(hdl32eAngularFiringCollector);
    Hdl32eRotationProvider hdl32eRotationProvider = new Hdl32eRotationProvider();
    hdl32eRotationProvider.addListener(hdl32eAngularFiringCollector);
    hdl32eFiringPacketDecoder.addListener(hdl32eSpacialProvider);
    hdl32eFiringPacketDecoder.addListener(hdl32eRotationProvider);
    hdl32eFiringPacketDecoder.addListener(new Hdl32eRealtimeFiringPacket(1));
    Hdl32eFiringFrame hdl32eFiringFrame = new Hdl32eFiringFrame();
    hdl32eAngularFiringCollector.addListener(hdl32eFiringFrame);
    hdl32ePositioningDecoder.addListener(hdl32eFiringFrame);
    return hdl32eFiringFrame;
  }
}
