// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.app.lidar.Hdl32ePanoramaWriter;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaListener;
import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.UserHome;

enum PcapHdl32ePanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePanoramaListener hdl32ePanoramaListener;
    // hdl32ePanoramaListener= new Hdl32ePanoramaFrame();
    hdl32ePanoramaListener = new Hdl32ePanoramaWriter(UserHome.Pictures("distances.gif"), 100, 640);
    PacketConsumer packetConsumer = new Hdl32ePacketConsumer( //
        new Hdl32ePanoramaCollector(hdl32ePanoramaListener) //
    );
    try {
      new PcapParse(Pcap.HIGHWAY.file, packetConsumer);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    hdl32ePanoramaListener.close();
  }
}
