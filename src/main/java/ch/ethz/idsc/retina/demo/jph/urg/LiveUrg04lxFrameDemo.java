// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ch.ethz.idsc.retina.dev.urg04lxug01.FileUrg04lxProvider;
import ch.ethz.idsc.retina.dev.urg04lxug01.RealtimeUrg04lxListener;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxFrame;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxProvider;

enum LiveUrg04lxFrameDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxProvider urg04lxProvider; // = LiveUrg04lxProvider.INSTANCE;
    urg04lxProvider = new FileUrg04lxProvider(Urg.LOG03.file);
    // ---
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame();
    urg04lxFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        urg04lxProvider.stop();
      }
    });
    // LiveUrgProvider.INSTANCE.addListener(UrgRecorder.createDefault());
    urg04lxProvider.addListener(urg04lxFrame);
    urg04lxProvider.addListener(new RealtimeUrg04lxListener(1.0));
    urg04lxProvider.start();
    urg04lxProvider.stop();
    System.out.println("stopped");
  }
}
