// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import java.io.File;

// TODO not public!
public enum Pcap {
  BUTTERFIELD(new File("/media/datahaki/media/ethz/hdl32e/usb/Velodyne/HDL-32E Sample Data", //
      "HDL32-V2_R into Butterfield into Digital Drive.pcap")), //
  TUNNEL(new File("/media/datahaki/media/ethz/hdl32e/usb/Velodyne/HDL-32E Sample Data", //
      "HDL32-V2_Tunnel.pcap")), //
  HIGHWAY(new File("/media/datahaki/media/ethz/hdl32e/usb/Velodyne/HDL-32E Sample Data", //
      "HDL32-V2_Monterey Highway.pcap")), //
  ;
  public final File file;

  private Pcap(File file) {
    this.file = file;
  }
}
