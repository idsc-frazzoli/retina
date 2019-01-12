// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Put;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum DavisDvsIntervalDistribution {
  ;
  public static void main(String[] args) throws Exception {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = DavisRecordings.OFFICE;
    cfg.speed_denominator = 4;
    LogPlayer lp = LogPlayer.create(cfg);
    DavisLcmClient davisLcmClient = new DavisLcmClient(DavisSerial.FX2_02460045.name());
    DavisDvsIntervalTracker davisDvsIntervalTracker = new DavisDvsIntervalTracker();
    davisLcmClient.addDvsListener(davisDvsIntervalTracker);
    davisLcmClient.startSubscriptions();
    lp.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent event) {
        Tensor bins = Tensors.vectorInt(davisDvsIntervalTracker.bins);
        try {
          Put.of(HomeDirectory.file("deltas.math"), bins);
          System.out.println("saved");
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        davisLcmClient.stopSubscriptions();
      }
    });
  }
}
