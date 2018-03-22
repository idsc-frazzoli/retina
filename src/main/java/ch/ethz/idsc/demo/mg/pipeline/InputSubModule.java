//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

/* For now, we process Offlinelogs. It should be very easy to switch to
 * live DVS events.
 */
public class InputSubModule implements OfflineLogListener, DavisDvsListener{
  final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private DavisSurfaceOfActiveEvents surface = new DavisSurfaceOfActiveEvents();
  private DavisDvsEvent davisDvsEvent;
  private boolean useFilter;
  

  private int backgroundActivityFilterTime = 500000; // the shorter the more is filtered[us]
  static double i, j; // for testing

  public InputSubModule(boolean useFilter) {
    davisDvsDatagramDecoder.addDvsListener(this);
    this.useFilter = useFilter;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    ++j;
    if (surface.backgroundActivityFilter(davisDvsEvent, backgroundActivityFilterTime) && useFilter) {
      // Here we can grab the filtered event stream
      this.davisDvsEvent = davisDvsEvent;
      
      ++i;
    }
    else {
      this.davisDvsEvent = davisDvsEvent;
    }
  }

  DavisDvsEvent getEvent() {
    return this.davisDvsEvent;
  }

  // simple functions for testing below.
  double getFilteredPercentage() {
    return 100 * (1 - i / j);
  }
}
