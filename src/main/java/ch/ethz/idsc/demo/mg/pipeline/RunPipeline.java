// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.mg.LogfileLocations;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;

// the log file that is used as input to the control pipeline can be chosen
public enum RunPipeline {
  ;
  public static void main(String[] args) throws IOException {
    File file = new File(LogfileLocations.DUBI8a);
    InputSubModule inputModule = new InputSubModule();
    OfflineLogPlayer.process(file, inputModule);
  }
}
