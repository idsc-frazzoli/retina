// code by jph, gjoel
package ch.ethz.idsc.gokart.core.track;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.gokart.lcm.mod.BSplineTrackLcm;
import ch.ethz.idsc.gokart.lcm.mod.Se2CurveLcm;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ref.TensorListener;

public class BSplineTrackLcmClient extends SimpleLcmClient<BSplineTrackListener> {
  public static BSplineTrackLcmClient open() {
    return new BSplineTrackLcmClient(GokartLcmChannel.XYR_TRACK_OPEN);
  }

  public static BSplineTrackLcmClient closed() {
    return new BSplineTrackLcmClient(GokartLcmChannel.XYR_TRACK_CLOSED);
  }

  // ---
  private BSplineTrackLcmClient(String channel) {
    super(channel);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    Optional<BSplineTrack> optional = BSplineTrackLcm.decode(channel, byteBuffer);
    listeners.forEach(tensorListener -> tensorListener.bSplineTrack(optional));
  }
}
