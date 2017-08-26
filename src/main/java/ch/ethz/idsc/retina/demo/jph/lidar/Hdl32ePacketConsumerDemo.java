// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.retina.dev.velodyne.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32ePcapPacketDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanorama;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaListener;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum Hdl32ePacketConsumerDemo {
  ;
  public static void main(String[] args) throws Exception {
    @SuppressWarnings("unused")
    LidarRayBlockListener hdl32ePositionListener = new LidarRayBlockListener() {
      @Override
      public void digest(FloatBuffer fb, ByteBuffer bb) {
        System.out.println("here");
      }
    };
    Hdl32ePanoramaListener hdl32ePanoramaListener = new Hdl32ePanoramaListener() {
      @Override
      public void panorama(Hdl32ePanorama hdl32ePanorama) {
        // System.out.println("here");
      }

      @Override
      public void close() {
        // ---
      }
    };
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaListener);
    Hdl32ePcapPacketDecoder packetConsumer = new Hdl32ePcapPacketDecoder();
    packetConsumer.hdl32eRayDecoder.addListener(hdl32ePanoramaCollector);
    PcapParse.of(Hdl32ePcap.TUNNEL.file, new PcapRealtimePlayback(1), packetConsumer);
  }
}
