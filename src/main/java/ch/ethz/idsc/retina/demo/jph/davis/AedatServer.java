// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.davis.io.aedat.AedatFileSocket;

// replays log file through UDP
enum AedatServer {
  ;
  public static void main(String[] args) throws Exception {
    AedatFileSocket aedatFileSocket = new AedatFileSocket(Aedat.LOG_03.file, 0.5);
    aedatFileSocket.start();
    aedatFileSocket.stop();
    System.out.println("stopped");
  }
}
