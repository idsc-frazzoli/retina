// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum PcapHdl32eImage {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    PacketConsumer packetConsumer = new Hdl32ePacketConsumer( //
        new Hdl32ePanoramaCollector(hdl32ePanoramaFrame) //
    );
    new PcapParse(new File( //
        "/media/datahaki/media/ethz/sensors/velodyne01/usb/Velodyne/HDL-32E Sample Data", //
        "HDL32-V2_R into Butterfield into Digital Drive.pcap"
    // "HDL32-V2_Tunnel.pcap"
    ), //
        packetConsumer);
  }
}
