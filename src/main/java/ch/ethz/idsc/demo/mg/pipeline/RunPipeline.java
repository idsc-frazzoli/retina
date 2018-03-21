package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.mg.LogfileLocations;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.app.DavisDetailViewer;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;

/* sets up the pipeline. Currently, it runs with a log file
 * as input.
 */
public enum RunPipeline {
  ;
  public static void main(String[] args) throws IOException {
    File file = UserHome.file(LogfileLocations.DUBI4);
    InputSubModule input = new InputSubModule();
    DavisBlobTracker trackerTest = new DavisBlobTracker();
    input.davisDvsDatagramDecoder.addDvsListener(input);
    // want to visualize the pipeline, how to incorporate the DavisDetailViewer?
    DavisDetailViewer davisDetailViewer = new DavisDetailViewer(GokartLcmChannel.DAVIS_OVERVIEW);
    davisDetailViewer.start();
    OfflineLogPlayer.process(file, input);
    // pass the (filtered) event from the inputsubmodule to the tracking module
    trackerTest.iterateThroughBlobs(input.getFilteredEvent());
    // test the backgroundActivityFilter
    System.out.println(input.getFilteredPercentage() + "% of the events have been noise filtered.");
  }
}
