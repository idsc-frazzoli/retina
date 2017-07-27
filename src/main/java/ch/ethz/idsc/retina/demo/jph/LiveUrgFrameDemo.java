// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.dev.urg04lx.FileUrgProvider;
import ch.ethz.idsc.retina.dev.urg04lx.LiveUrgProvider;
import ch.ethz.idsc.retina.dev.urg04lx.UrgFrame;
import ch.ethz.idsc.retina.dev.urg04lx.UrgProvider;

enum LiveUrgFrameDemo {
  ;
  public static void main(String[] args) throws Exception {
    UrgProvider urgProvider = LiveUrgProvider.INSTANCE;
    urgProvider = new FileUrgProvider( //
        new File("/media/datahaki/media/ethz/urg04lx", "urg20170727T133009.txt"));
    // ---
    UrgFrame urgFrame = new UrgFrame(urgProvider);
    // LiveUrgProvider.INSTANCE.addListener(UrgRecorder.createDefault());
    urgProvider.addListener(urgFrame);
    urgProvider.start();
  }
}
