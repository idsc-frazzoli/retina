// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisEventProvider;
import ch.ethz.idsc.retina.dev.davis._240c.EventRealtimeSleeper;
import ch.ethz.idsc.retina.dvs.io.aedat.AedatFileSupplier;
import ch.ethz.idsc.retina.dvs.io.aps.ApsImageSocket;

enum AedatImageServerDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = ApsImageSocket.INSTANCE.davisDecoder;
    DavisEventProvider davisEventProvider = new AedatFileSupplier(Aedat.LOG_04.file, davisDecoder);
    davisDecoder.addListener(new EventRealtimeSleeper(1.0));
    davisEventProvider.start();
    davisEventProvider.stop();
    System.out.println("server out of data");
  }
}
