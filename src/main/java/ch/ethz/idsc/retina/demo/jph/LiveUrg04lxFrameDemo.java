// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.dev.urg04lx.LiveUrg04lxProvider;
import ch.ethz.idsc.retina.dev.urg04lx.Urg04lxFrame;
import ch.ethz.idsc.retina.dev.urg04lx.Urg04lxProvider;

enum LiveUrg04lxFrameDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxProvider urg04lxProvider = LiveUrg04lxProvider.INSTANCE;
    // urg04lxProvider = new FileUrg04lxProvider( //
    // new File("/media/datahaki/media/ethz/urg04lx", "urg20170727T133009.txt"));
    // ---
    Urg04lxFrame urgFrame = new Urg04lxFrame(urg04lxProvider);
    // LiveUrgProvider.INSTANCE.addListener(UrgRecorder.createDefault());
    urg04lxProvider.addListener(urgFrame);
    urg04lxProvider.start();
  }
}
