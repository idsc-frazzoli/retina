package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;

enum SanityCheck {
  ;
  public static void main(String[] args) throws IOException {
    File file = new File("/home/datahaki/gokart/pursuit/20180112T154355/log.lcm");
    OfflineLogPlayer.process(file, MessageConsistency.INSTANCE);
  }
}
