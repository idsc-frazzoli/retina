// code by az
package ch.ethz.idsc.demo.az;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.io.HomeDirectory;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    file = HomeDirectory.file("datasets/gokart_logs/20180412T152900_7e5b46c2.lcm.00");
    file = HomeDirectory.file("datasets/gokart_logs/20180423T181849_633cc6e6.lcm.00");
    System.out.println(file.isFile());
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 3;
    LogPlayer.create(cfg);
  }
}
