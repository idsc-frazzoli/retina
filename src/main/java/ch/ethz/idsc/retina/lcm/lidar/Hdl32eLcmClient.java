// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePosDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRayDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.VelodyneModel;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

/** reference implementation of an lcm client that listens and decodes
 * hdl32e publications and allows listeners to receive the data */
// TODO OWLY3D uses class
public class Hdl32eLcmClient implements LcmClientInterface {
  public final Hdl32eRayDecoder hdl32eRayDecoder = new Hdl32eRayDecoder();
  public final Hdl32ePosDecoder hdl32ePosDecoder = new Hdl32ePosDecoder();
  private final String lidarId;

  public Hdl32eLcmClient(String lidarId) {
    this.lidarId = lidarId;
  }

  @Override
  public void startSubscriptions() {
    LCM lcm = LCM.getSingleton();
    if (hdl32eRayDecoder.hasListeners())
      lcm.subscribe(VelodyneLcmChannels.ray(VelodyneModel.HDL32E, lidarId), new LCMSubscriber() {
        @Override
        public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
          try {
            BinaryBlob binaryBlob = new BinaryBlob(ins);
            ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            hdl32eRayDecoder.lasers(byteBuffer);
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      });
    if (hdl32ePosDecoder.hasListeners())
      lcm.subscribe(VelodyneLcmChannels.pos(VelodyneModel.HDL32E, lidarId), new LCMSubscriber() {
        @Override
        public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
          try {
            BinaryBlob binaryBlob = new BinaryBlob(ins);
            ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            hdl32ePosDecoder.positioning(byteBuffer);
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      });
  }
}
