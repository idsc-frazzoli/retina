// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.davis.DavisDvsBlockPublisher;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.AbstractAccumulatedImage;
import ch.ethz.idsc.retina.davis.app.SAEGaussDecayImage;
import ch.ethz.idsc.retina.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.HomeDirectory;

class AccumulateToListener implements OfflineLogListener, TimedImageListener {
  private final String CHANNEL = DavisDvsBlockPublisher.channel("overview");
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private int count = 0;

  public AccumulateToListener(int period) {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    // handle dvs
    AbstractAccumulatedImage accumulatedEventsImage = SAEGaussDecayImage.of(davisDevice, period);
    davisDvsDatagramDecoder.addDvsListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(this);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(CHANNEL)) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override // from TimedImageListener
  public void timedImage(TimedImageEvent timedImageEvent) {
    ++count;
    // System.out.println("asd " + timedImageEvent.time);
  }

  public static void main(String[] args) throws IOException {
    File file = HomeDirectory.file("gokart/twist/20180108T165210_1/log.lcm");
    AccumulateToListener oll = new AccumulateToListener(100);
    OfflineLogPlayer.process(file, oll);
    System.out.println(oll.count);
  }
}
