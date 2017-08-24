// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringPacketDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRealtimeFiringPacket;
import ch.ethz.idsc.retina.dev.hdl32e.app.Hdl32eFiringFrame;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eAngularFiringCollector;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eRotationProvider;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eSpacialProvider;
import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum Hdl32ePcapFiringDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32eFiringFrame hdl32eFiringFrame = new Hdl32eFiringFrame();
    FloatBuffer floatBuffer = FloatBuffer.wrap(new float[2310 * 32 * 3]);
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[2310 * 32]);
    Hdl32eAngularFiringCollector hdl32eAngularFiringCollector = //
        new Hdl32eAngularFiringCollector(floatBuffer, byteBuffer);
    Hdl32eFiringPacketDecoder hdl32eFiringPacketConsumer = new Hdl32eFiringPacketDecoder();
    Hdl32eSpacialProvider hdl32eSpacialProvider = new Hdl32eSpacialProvider();
    hdl32eSpacialProvider.addListener(hdl32eAngularFiringCollector);
    Hdl32eRotationProvider hdl32eRotationProvider = new Hdl32eRotationProvider();
    hdl32eRotationProvider.addListener(hdl32eAngularFiringCollector);
    hdl32eFiringPacketConsumer.addListener(hdl32eSpacialProvider);
    hdl32eFiringPacketConsumer.addListener(hdl32eRotationProvider);
    hdl32eFiringPacketConsumer.addListener(new Hdl32eRealtimeFiringPacket(1));
    hdl32eAngularFiringCollector.addListener(hdl32eFiringFrame);
    PcapPacketConsumer packetConsumer = new Hdl32ePacketConsumer(hdl32eFiringPacketConsumer, null);
    PcapParse.of(Pcap.HIGHWAY.file, packetConsumer); // blocking
  }
}
