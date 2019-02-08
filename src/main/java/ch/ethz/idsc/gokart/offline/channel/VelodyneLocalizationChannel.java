// code by mh
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.util.gps.WGS84toCH1903LV03Plus;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

// TODO export more info
public enum VelodyneLocalizationChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelInterface
  public String channel() {
    return VelodyneLcmChannels.pos(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  }

  @Override // from SingleChannelInterface
  public Tensor row(ByteBuffer byteBuffer) {
    VelodynePosEvent velodynePosEvent = VelodynePosEvent.vlp16(byteBuffer);
    Scalar degX = velodynePosEvent.gpsX();
    Scalar degY = velodynePosEvent.gpsY();
    Tensor metric = WGS84toCH1903LV03Plus.transform(degX, degY);
    return Tensors.of( //
        // degX.map(Magnitude.DEGREE_ANGLE).map(Round._6), //
        // degY.map(Magnitude.DEGREE_ANGLE).map(Round._6), //
        metric.map(Magnitude.METER).map(Round._2), //
        velodynePosEvent.speed().map(Magnitude.VELOCITY).map(Round._3), //
        velodynePosEvent.course().map(Magnitude.ONE).map(Round._6));
  }
}
