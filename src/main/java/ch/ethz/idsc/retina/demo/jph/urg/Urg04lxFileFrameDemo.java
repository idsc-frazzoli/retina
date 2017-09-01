// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxFileProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxRealtimeListener;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.app.Urg04lxFrame;

enum Urg04lxFileFrameDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxProvider urg04lxProvider = new Urg04lxFileProvider(Urg.LOG05.file);
    // ---
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame();
    urg04lxFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        urg04lxProvider.stop();
      }
    });
    urg04lxProvider.addListener(urg04lxFrame);
    urg04lxProvider.addListener(new Urg04lxRealtimeListener(1.0));
    urg04lxProvider.start();
    urg04lxProvider.stop();
  }
}
