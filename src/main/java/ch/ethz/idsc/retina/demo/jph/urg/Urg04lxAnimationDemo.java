// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import java.awt.Dimension;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxFileProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.app.Urg04lxAnimationWriter;
import ch.ethz.idsc.retina.util.io.UserHome;

enum Urg04lxAnimationDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxFileProvider urg04lxProvider = new Urg04lxFileProvider(Urg.LOG05.file);
    // ---
    // Urg04lxFrame urg04lxFrame = new Urg04lxFrame(urg04lxProvider);
    Urg04lxAnimationWriter urgAnimationWriter = //
        new Urg04lxAnimationWriter(UserHome.Pictures("urg04lx.gif"), 100, new Dimension(320, 260));
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
