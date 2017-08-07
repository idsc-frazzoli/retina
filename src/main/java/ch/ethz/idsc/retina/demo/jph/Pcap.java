// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

enum Pcap {
  BUTTERFIELD(new File("/media/datahaki/media/ethz/sensors/velodyne01/usb/Velodyne/HDL-32E Sample Data", //
      "HDL32-V2_R into Butterfield into Digital Drive.pcap")), //
  TUNNEL(new File("/media/datahaki/media/ethz/sensors/velodyne01/usb/Velodyne/HDL-32E Sample Data", //
      "HDL32-V2_Tunnel.pcap")), //
  HIGHWAY(new File("/media/datahaki/media/ethz/sensors/velodyne01/usb/Velodyne/HDL-32E Sample Data", //
      "HDL32-V2_Monterey Highway.pcap")), //
  ;
  public final File file;

  private Pcap(File file) {
    this.file = file;
  }
}
