// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import java.awt.Dimension;

import ch.ethz.idsc.retina.urg04lxug01.FileUrg04lxProvider;
import ch.ethz.idsc.retina.urg04lxug01.UrgAnimationWriter;
import ch.ethz.idsc.retina.util.io.UserHome;

enum Urg04lxAnimationDemo {
  ;
  public static void main(String[] args) throws Exception {
    FileUrg04lxProvider urg04lxProvider = new FileUrg04lxProvider(Urg.LOG05.file);
    // ---
    // Urg04lxFrame urg04lxFrame = new Urg04lxFrame(urg04lxProvider);
    UrgAnimationWriter urgAnimationWriter = //
        new UrgAnimationWriter(UserHome.Pictures("urg04lx.gif"), 100, new Dimension(320, 260));
    // LiveUrgProvider.INSTANCE.addListener(UrgRecorder.createDefault());
    urg04lxProvider.addListener(urgAnimationWriter);
    urg04lxProvider.start(); // TODO this could be a blocking call?
    while (urgAnimationWriter.frameCount() < 200)
      Thread.sleep(100);
    urg04lxProvider.stop();
    // while (!urg04lxProvider.isTerminated())
    urgAnimationWriter.close();
  }
}
