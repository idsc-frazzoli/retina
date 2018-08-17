// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.blobtrack.algo.BlobTrackProvider;
import ch.ethz.idsc.demo.mg.blobtrack.vis.BlobTrackViewer;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

/** wrapper to run the blob tracking algorithm offline */
/* package */ class OfflineBlobTrackWrap implements OfflineLogListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  // specific to blob tracking algorithm
  private final BlobTrackProvider blobTrackProvider;
  private final BlobTrackViewer blobTrackViewer;

  OfflineBlobTrackWrap(BlobTrackConfig blobTrackConfig) {
    blobTrackProvider = new BlobTrackProvider(blobTrackConfig);
    blobTrackViewer = new BlobTrackViewer(blobTrackConfig, blobTrackProvider);
    davisDvsDatagramDecoder.addDvsListener(blobTrackProvider);
    davisDvsDatagramDecoder.addDvsListener(blobTrackViewer);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs"))
      davisDvsDatagramDecoder.decode(byteBuffer);
  }
}
