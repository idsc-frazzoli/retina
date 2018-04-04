//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.gui.PipelineVisualization;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// submodule filters event stream
public class InputSubModule implements OfflineLogListener, DavisDvsListener {
  // parameters
  private final int maxEventCount = 500;
  private final int backgroundActivityFilterTime = 1000; // [us] the shorter the more is filtered
  private final int imageInterval = 1000; // [us]
  private final boolean useFilter = true;
  // objects required for the module
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();;
  private final DavisSurfaceOfActiveEvents surface = new DavisSurfaceOfActiveEvents();;
  private final DavisBlobTracker track = new DavisBlobTracker();;
  private final PipelineVisualization viz = new PipelineVisualization();;
  // fields for testing
  private int eventCount;
  private int filteredEventCount;
  private int lastTimestamp;
  private int begin, end;

  public InputSubModule() {
    davisDvsDatagramDecoder.addDvsListener(this);
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (eventCount == 0) {
      begin = davisDvsEvent.time;
    }
    ++eventCount;
    // long startTime = System.nanoTime();
    track.receiveNewEvent(davisDvsEvent);
    // long endTime = System.nanoTime();
    // long elapsedTime = endTime-startTime;
    // System.out.println("Elapsed Time: "+elapsedTime);
    if ((davisDvsEvent.time - lastTimestamp) > imageInterval) {
      // try {
      // //viz.generateImage(track.getActiveBlobs());
      // } catch (IOException e) {
      // e.printStackTrace();
      // }
      lastTimestamp = davisDvsEvent.time;
    }
    if (surface.backgroundActivityFilter(davisDvsEvent, backgroundActivityFilterTime) && useFilter) {
      ++filteredEventCount;
    }
    if (eventCount > maxEventCount) {
      end = davisDvsEvent.time;
      int diff = end - begin;
      System.out.println("Elapsed time [us]: " + diff + " with " + eventCount + " events");
      System.exit(0);
    }
  }

  public double getFilteredPercentage() {
    return 100 * (1 - filteredEventCount / eventCount);
  }
}
