// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.davis.DavisDvsBlockPublisher;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum DavisDvsChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelInterface
  public String channel() {
    return DavisDvsBlockPublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  }

  @Override // from SingleChannelInterface
  public Tensor row(ByteBuffer byteBuffer) {
    int[] polarity = new int[2];
    DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
    davisDvsDatagramDecoder.addDvsListener(new DavisDvsListener() {
      @Override
      public void davisDvs(DavisDvsEvent davisDvsEvent) {
        ++polarity[davisDvsEvent.i];
      }
    });
    davisDvsDatagramDecoder.decode(byteBuffer);
    return Tensors.vectorInt(polarity);
  }
}
