// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.util.gps.Gprmc;
import ch.ethz.idsc.retina.util.gps.WGS84toCH1903LV03Plus;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

public enum VelodynePosChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  private static final String CHANNEL = //
      VelodyneLcmChannels.pos(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);

  @Override // from SingleChannelInterface
  public String channel() {
    return CHANNEL;
  }

  @Override // from SingleChannelInterface
  public Tensor row(ByteBuffer byteBuffer) {
    VelodynePosEvent velodynePosEvent = VelodynePosEvent.vlp16(byteBuffer);
    Gprmc gprmc = velodynePosEvent.gprmc();
    Scalar degX = gprmc.gpsX();
    Scalar degY = gprmc.gpsY();
    Tensor metric = WGS84toCH1903LV03Plus.transform(degX, degY);
    return Tensors.of( //
        RealScalar.of(velodynePosEvent.gps_usec()), //
        // degX.map(Magnitude.DEGREE_ANGLE).map(Round._6), //
        // degY.map(Magnitude.DEGREE_ANGLE).map(Round._6), //
        metric.map(Magnitude.METER).map(Round._2), //
        gprmc.speed().map(Magnitude.VELOCITY).map(Round._3), //
        gprmc.course().map(Magnitude.ONE).map(Round._6));
  }
}
