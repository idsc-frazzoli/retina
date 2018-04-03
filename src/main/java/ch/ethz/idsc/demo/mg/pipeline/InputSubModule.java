//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.gui.PipelineVisualization;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.app.AbstractAccumulatedImage;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsGrayImage;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// submodule filters event stream and allows visualization
public class InputSubModule implements OfflineLogListener, DavisDvsListener {
  // parameters
  private final int maxEventCount = 100000000;
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

  public InputSubModule() {
    davisDvsDatagramDecoder.addDvsListener(this);
    // we probably need more listeners here
    DavisDevice davisDevice = Davis240c.INSTANCE;
    AbstractAccumulatedImage abstractAccumulatedImage = AccumulatedEventsGrayImage.of(davisDevice);
    davisDvsDatagramDecoder.addDvsListener(abstractAccumulatedImage);
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    ++eventCount;
    track.receiveNewEvent(davisDvsEvent);
    if ((davisDvsEvent.time - lastTimestamp) > imageInterval) {
//      try {
//        //viz.generateImage(track.getActiveBlobs());
//        // generateImage();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
      lastTimestamp = davisDvsEvent.time;
    }
    if (surface.backgroundActivityFilter(davisDvsEvent, backgroundActivityFilterTime) && useFilter) {
      ++filteredEventCount;
    }
    if (eventCount > maxEventCount) {
      System.exit(0);
    }
  }

  public double getFilteredPercentage() {
    return 100 * (1 - filteredEventCount / eventCount);
  }
}
