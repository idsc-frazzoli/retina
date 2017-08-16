// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.core.StartAndStoppable;
import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis._240c.DavisRealtimeSleeper;
import ch.ethz.idsc.retina.dev.davis.app.DavisDefaultDatagramServer;
import ch.ethz.idsc.retina.dev.davis.io.AedatFileSupplier;

enum AedatImageServerDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = DavisDefaultDatagramServer.INSTANCE.davisDecoder;
    StartAndStoppable davisEventProvider = new AedatFileSupplier(Aedat.LOG_04.file, davisDecoder);
    davisDecoder.addListener(new DavisRealtimeSleeper(1.0));
    davisEventProvider.start();
    davisEventProvider.stop();
    System.out.println("server out of data");
  }
}
