// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import java.io.File;

enum Vlp16Pcap {
  DOWNTOWN_SINGLE(new File("/media/datahaki/media/ethz/vlp16/VELODYNE/VLP-16 Sample Data", //
      "2015-07-23-14-37-22_Velodyne-VLP-16-Data_Downtown 10Hz Single.pcap")), //
  DEPOT_SINGLE(new File("/media/datahaki/media/ethz/vlp16/VELODYNE/VLP-16 Sample Data", //
      "2015-07-23-15-08-34_Velodyne-VLP-16-Data_Depot 10Hz Single Returns.pcap")), //
  ;
  final File file;

  private Vlp16Pcap(File file) {
    this.file = file;
  }
}
