// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.velodyne.app.VelodynePcapPacketDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum Hdl32ePcapPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    VelodynePcapPacketDecoder packetConsumer = VelodynePcapPacketDecoder.hdl32e();
    Hdl32eDecoder rdec = (Hdl32eDecoder) packetConsumer.velodyneDecoder;
    rdec.addRayListener(hdl32ePanoramaCollector);
    rdec.addPosListener(hdl32ePanoramaFrame);
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, new PcapRealtimePlayback(1), packetConsumer); // blocking
  }
}
