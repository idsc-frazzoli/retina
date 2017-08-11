// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ch.ethz.idsc.retina.urg04lxug01.FileUrg04lxProvider;
import ch.ethz.idsc.retina.urg04lxug01.Urg04lxFrame;
import ch.ethz.idsc.retina.urg04lxug01.Urg04lxProvider;
import ch.ethz.idsc.retina.urg04lxug01.Urg04lxRealtimeListener;

enum Urg04lxFileFrameDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxProvider urg04lxProvider = new FileUrg04lxProvider(Urg.LOG05.file);
    // ---
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame();
    urg04lxFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        urg04lxProvider.stop();
      }
    });
    urg04lxProvider.addListener(urg04lxFrame);
    urg04lxProvider.addListener(new Urg04lxRealtimeListener(0.5));
    urg04lxProvider.start();
    urg04lxProvider.stop();
  }
}
