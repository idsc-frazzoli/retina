// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import java.io.File;

enum Hdl32ePcap {
  BUTTERFIELD(new File("/media/datahaki/media/ethz/hdl32e/usb/Velodyne/HDL-32E Sample Data", //
      "HDL32-V2_R into Butterfield into Digital Drive.pcap")), //
  TUNNEL(new File("/media/datahaki/media/ethz/hdl32e/usb/Velodyne/HDL-32E Sample Data", //
      "HDL32-V2_Tunnel.pcap")), //
  HIGHWAY(new File("/media/datahaki/media/ethz/hdl32e/usb/Velodyne/HDL-32E Sample Data", //
      "HDL32-V2_Monterey Highway.pcap")), //
  ;
  final File file;

  private Hdl32ePcap(File file) {
    this.file = file;
  }
}
