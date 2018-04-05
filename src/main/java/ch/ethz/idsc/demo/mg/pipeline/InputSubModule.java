//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.gui.PipelineFrame;
import ch.ethz.idsc.demo.mg.gui.PipelineVisualization;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// submodule filters event stream
public class InputSubModule implements OfflineLogListener, DavisDvsListener {
  // parameters
  private final int maxEventCount = 1000000;
  private final int backgroundActivityFilterTime = 2500; // [us] the shorter the more is filtered
  private final int imageInterval = 33000; // [us] 33Hz
  private final boolean useFilter = true;
  // objects required for the module
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final DavisSurfaceOfActiveEvents surface = new DavisSurfaceOfActiveEvents();
  private final DavisBlobTracker track = new DavisBlobTracker();
  // below for visualization
  private final PipelineVisualization viz = new PipelineVisualization();
  private final PipelineFrame[] frames = new PipelineFrame[2];
  // fields for testing
  private float eventCount;
  private float filteredEventCount;
  private int lastTimestamp;
  private int begin, end;
  private long startTime, endTime;

  public InputSubModule() {
    davisDvsDatagramDecoder.addDvsListener(this);
    frames[0] = new PipelineFrame();
    frames[1] = new PipelineFrame();
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    // start timer
    if (eventCount == 0) {
      begin = davisDvsEvent.time;
      lastTimestamp = davisDvsEvent.time;
      startTime = System.currentTimeMillis();
    }
    ++eventCount;
    // send event to tracker and image frame
    track.receiveNewEvent(davisDvsEvent);
    frames[0].receiveEvent(davisDvsEvent);
    // send filtered events to image frame
    if (surface.backgroundActivityFilter(davisDvsEvent, backgroundActivityFilterTime) && useFilter) {
      frames[1].receiveEvent(davisDvsEvent);
      ++filteredEventCount;
    }
    // the events are accumulated for the interval time and then displayed in a single frame
    if ((davisDvsEvent.time - lastTimestamp) > imageInterval) {
      viz.setImage(frames[0].getAccumulatedEvents(), 0);
      viz.setImage(frames[1].trackOverlay(track.getBlobList(1)), 1);
      try {
        viz.saveImages();
        track.printStatusUpdate(davisDvsEvent);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      frames[0].clearImage();
      frames[1].clearImage();
      lastTimestamp = davisDvsEvent.time;
    }
    if (eventCount > maxEventCount) {
      end = davisDvsEvent.time;
      endTime = System.currentTimeMillis();
      int diff = end - begin;
      System.out.println("Elapsed time in the eventstream [ms]: " + diff / 1000 + " with " + eventCount + " events");
      long elapsedTime = endTime - startTime;
      System.out.println("Computation time: " + elapsedTime + "[ms]");
      System.out.format("%.2f%% of the events were processed after filtering.", getFilteredPercentage());
      System.exit(0);
    }
  }

  public float getFilteredPercentage() {
    return 100 * filteredEventCount / eventCount;
  }
}
