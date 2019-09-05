// code by jph, gjoel
package ch.ethz.idsc.gokart.lcm.mod;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.core.track.BSplineTrackCyclic;
import ch.ethz.idsc.gokart.core.track.BSplineTrackString;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.ArrayFloatBlob;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public enum BSplineTrackLcm {
  ;
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

  public static BSplineTrack decode(String channel, ByteBuffer byteBuffer) {
    return channel.endsWith(".c") //
        ? new BSplineTrackCyclic(decode(byteBuffer)) //
        : new BSplineTrackString(decode(byteBuffer));
  }
}
