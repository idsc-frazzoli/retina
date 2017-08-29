// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxFrame;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxLiveProvider;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxProvider;

enum Urg04lxLiveFrameDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxProvider urg04lxProvider = Urg04lxLiveProvider.INSTANCE;
    // ---
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame();
    urg04lxFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        urg04lxProvider.stop();
      }
    });
    urg04lxProvider.addListener(urg04lxFrame);
    urg04lxProvider.start();
  }
}
