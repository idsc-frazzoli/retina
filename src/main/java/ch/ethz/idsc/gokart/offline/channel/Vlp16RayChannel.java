// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** channel interface does not export all information provided by the VLP16
 * but only extracts the timestamp and first rotational/azimuth value of a ray packet */
public enum Vlp16RayChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelInterface
  public String channel() {
    return VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  }

  @Override // from SingleChannelInterface
  public Tensor row(ByteBuffer byteBuffer) {
    int[] values = new int[] { -1, -1 };
    Vlp16Decoder vlp16Decoder = new Vlp16Decoder();
    vlp16Decoder.addRayListener(new LidarRayDataListener() {
      boolean flag = true;

      @Override // from LidarRayDataListener
      public void timestamp(int usec, int type) {
        values[0] = usec;
      }

      @Override // from LidarRayDataListener
      public void scan(int rotational, ByteBuffer byteBuffer) {
        if (flag) {
          values[1] = rotational;
          flag = false;
        }
      }
    });
    vlp16Decoder.lasers(byteBuffer);
    return Tensors.vectorInt(values);
  }
}
