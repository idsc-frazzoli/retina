// code by jph
package ch.ethz.idsc.demo.ni;

import java.io.File;
import java.io.IOException;

import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    // File file = new File("C:\\Users\\maste_000\\Documents\\ETH\\LogFilesKart\\1218", "20171218T114547_9b56b71b.lcm.00");
    // File file = new File("C:\\Users\\maste_000\\Documents\\ETH\\LogFilesKart\\1218", "20171218T112805_9b56b71b.lcm.00");
    File file = new File("C:\\Users\\maste_000\\Documents\\ETH\\LogFilesKart\\1218", "20171218T121006_9b56b71b.lcm.00.extract");
    // File file = new File("/home/datahaki", "20171218T130515_4794c081.lcm.00");
    cfg.logFile = file.toString();
    LogPlayer.create(cfg);
  }
}
