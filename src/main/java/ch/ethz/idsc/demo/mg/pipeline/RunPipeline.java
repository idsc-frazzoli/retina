package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.mg.LogfileLocations;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;

/* sets up the pipeline. Currently, it runs with a log file
 * as input.
 */
public enum RunPipeline {
  ;
  public static void main(String[] args) throws IOException {
    boolean useFilter = true;
    File file = UserHome.file(LogfileLocations.DUBI4a);
    InputSubModule input = new InputSubModule(useFilter);
    // commented out by Jan!
    // input.davisDvsDatagramDecoder.addDvsListener(input);
    OfflineLogPlayer.process(file, input);
    // only for testing
    // test the backgroundActivityFilter
    if (useFilter) {
      System.out.println(String.format("%.2f", input.getFilteredPercentage()) + "% of the events have been noise filtered.");
    }
  }
}
