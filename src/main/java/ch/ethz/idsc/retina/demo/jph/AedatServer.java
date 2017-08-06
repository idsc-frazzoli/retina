// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dvs.io.aedat.AedatFileSocket;

public enum AedatServer {
  ;
  public static void main(String[] args) throws Exception {
    AedatFileSocket aedatFileChannel = new AedatFileSocket(Datahaki.LOG_03.file, Davis240c.INSTANCE.createDecoder());
    aedatFileChannel.start();
    aedatFileChannel.stop();
    System.out.println("stoped");
  }
}
