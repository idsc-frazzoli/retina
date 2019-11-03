// code by gjoel
package ch.ethz.idsc.gokart.core.track;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.gokart.lcm.mod.BSplineTrackLcm;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

public class BSplineTrackLcmClient extends SimpleLcmClient<BSplineTrackListener> {
  /** @return */
  public static BSplineTrackLcmClient string() {
    return new BSplineTrackLcmClient(GokartLcmChannel.XYR_TRACK_STRING);
  }

  /** @return */
  public static BSplineTrackLcmClient cyclic() {
    return new BSplineTrackLcmClient(GokartLcmChannel.XYR_TRACK_CYCLIC);
  }

  // ---
  private BSplineTrackLcmClient(String channel) {
    super(channel);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    TrackReconModule trackReconModule = ModuleAuto.INSTANCE.getInstance(TrackReconModule.class);
    Optional<BSplineTrack> optional;
    if (Objects.nonNull(trackReconModule))
      optional = trackReconModule.bSplineTrack();
    else
      optional = BSplineTrackLcm.decode(channel, byteBuffer);
    listeners.forEach(tensorListener -> tensorListener.bSplineTrack(optional));
  }
}
