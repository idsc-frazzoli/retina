// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanorama;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaListener;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePositionListener;
import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

/** collects array of 3d positions */
enum Hdl32ePacketConsumerDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePositionListener hdl32ePositionListener = new Hdl32ePositionListener() {
      @Override
      public void digest(float[] position_data, int length) {
        // System.out.println("here");
      }
    };
    Hdl32ePanoramaListener hdl32ePanoramaListener = new Hdl32ePanoramaListener() {
      @Override
      public void panorama(Hdl32ePanorama hdl32ePanorama) {
        // System.out.println(hdl32ePanorama.angle);
        // System.out.println("here" + Dimensions.of(hdl32ePanorama.distances));
        // System.out.println("here" + Dimensions.of(hdl32ePanorama.intensity));
      }
    };
    PacketConsumer packetConsumer = new Hdl32ePacketConsumer( //
        // new Hdl32ePositionCollector(hdl32ePositionListener) //
        new Hdl32ePanoramaCollector(hdl32ePanoramaListener) //
    );
    new PcapParse(new File( //
        "/media/datahaki/media/ethz/sensors/velodyne01/usb/Velodyne/HDL-32E Sample Data", //
        "HDL32-V2_Tunnel.pcap"), //
        packetConsumer);
  }
}
