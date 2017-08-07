// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import ch.ethz.idsc.retina.dev.urg04lx.FileUrg04lxProvider;
import ch.ethz.idsc.retina.dev.urg04lx.LiveUrg04lxProvider;
import ch.ethz.idsc.retina.dev.urg04lx.Urg04lxFrame;
import ch.ethz.idsc.retina.dev.urg04lx.Urg04lxProvider;

enum LiveUrg04lxFrameDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxProvider urg04lxProvider = LiveUrg04lxProvider.INSTANCE;
    urg04lxProvider = new FileUrg04lxProvider(Urg.LOG03.file);
    // ---
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame(urg04lxProvider);
    // LiveUrgProvider.INSTANCE.addListener(UrgRecorder.createDefault());
    urg04lxProvider.addListener(urg04lxFrame);
    urg04lxProvider.start();
  }
}
