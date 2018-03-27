//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

/** For now, we process Offlinelogs. It should be very easy to switch to
 * live DVS events. */
public class InputSubModule implements OfflineLogListener, DavisDvsListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private DavisSurfaceOfActiveEvents surface = new DavisSurfaceOfActiveEvents();
  // below for testing
  private boolean useFilter;
  private int backgroundActivityFilterTime = 1000; // [us] the shorter the more is filtered
  private double I, J;
  private DavisBlobTracker track; // to send events to next module

  public InputSubModule(boolean useFilter) {
    davisDvsDatagramDecoder.addDvsListener(this);
    this.useFilter = useFilter;
    track = new DavisBlobTracker();
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    ++J;
    // because 1) "davisDvsDatagramDecoder.addDvsListener(this);"
    // and 2) "input.davisDvsDatagramDecoder.addDvsListener(input);"
    // if (J % 2 == 0)
    {
      track.receiveNewEvent(davisDvsEvent); // send events to next module
    }
    if (surface.backgroundActivityFilter(davisDvsEvent, backgroundActivityFilterTime) && useFilter) {
      // Here we can grab the filtered event stream
      ++I;
    }
    // only run algorithm for a few events and see what is happening
    if (J > 400) {
      System.exit(0);
    }
  }

  // simple functions for testing below.
  public double getFilteredPercentage() {
    return 100 * (1 - I / J);
  }
}
