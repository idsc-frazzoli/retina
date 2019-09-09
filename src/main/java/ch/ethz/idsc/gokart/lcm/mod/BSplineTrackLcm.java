// code by jph, gjoel
package ch.ethz.idsc.gokart.lcm.mod;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.ArrayFloatBlob;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public enum BSplineTrackLcm {
  ;
  public static void publish(Optional<BSplineTrack> optional) {
    if (optional.isPresent())
      publish(optional.get());
    else
      LCM.getSingleton().publish(GokartLcmChannel.XYR_TRACK_OPEN, ArrayFloatBlob.encode(Tensors.empty()));
  }

  /** @param bSplineTrack */
  public static void publish(BSplineTrack bSplineTrack) {
    LCM.getSingleton().publish(bSplineTrack.isClosed() //
            ? GokartLcmChannel.XYR_TRACK_CLOSED //
            : GokartLcmChannel.XYR_TRACK_OPEN, //
        encode(bSplineTrack));
  }

  /** @param channel
   * @param bSplineTrack */
  public static void publish(String channel, BSplineTrack bSplineTrack) {
    LCM.getSingleton().publish(channel, encode(bSplineTrack));
  }

  /** @param bSplineTrack
   * @return */
  public static BinaryBlob encode(BSplineTrack bSplineTrack) {
    Tensor xyr = bSplineTrack.combinedControlPoints().map(Magnitude.METER);
    return ArrayFloatBlob.encode(xyr);
  }

  /** @param byteBuffer
   * @return tensor with rows of the form {x[m], y[m], radius[m]} */
  public static Tensor decode(ByteBuffer byteBuffer) {
    return ArrayFloatBlob.decode(byteBuffer).map(scalar -> Quantity.of(scalar, SI.METER));
  }

  public static Optional<BSplineTrack> decode(String channel, ByteBuffer byteBuffer) {
    Tensor xyr = decode(byteBuffer);
    if (Tensors.isEmpty(xyr))
      return Optional.empty();
    return Optional.of(BSplineTrack.of(xyr, channel.endsWith(".c")));
  }
}
