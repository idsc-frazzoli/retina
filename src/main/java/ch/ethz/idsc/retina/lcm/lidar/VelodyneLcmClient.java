// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.velodyne.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.velodyne.VelodyneModel;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.dev.velodyne.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

/** reference implementation of an lcm client that listens and decodes
 * hdl32e publications and allows listeners to receive the data
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class VelodyneLcmClient implements LcmClientInterface {
  public static VelodyneLcmClient hdl32e(String lidarId) {
    return new VelodyneLcmClient(VelodyneModel.HDL32E, lidarId);
  }

  public static VelodyneLcmClient vlp16(String lidarId) {
    return new VelodyneLcmClient(VelodyneModel.VLP16, lidarId);
  }

  // ---
  // public final VelodyneRayDecoder rayDecoder;
  public final VelodyneDecoder posDecoder;
  private final VelodyneModel velodyneModel;
  private final String lidarId;

  private VelodyneLcmClient(VelodyneModel velodyneModel, String lidarId) {
    this.velodyneModel = velodyneModel;
    this.lidarId = lidarId;
    switch (velodyneModel) {
    case HDL32E:
      // rayDecoder = new Hdl32eRayDecoder();
      posDecoder = new Hdl32eDecoder();
      break;
    case VLP16:
      // rayDecoder = new Vlp16RayDecoder();
      posDecoder = new Vlp16Decoder();
      break;
    default:
      throw new RuntimeException();
    }
  }

  @Override
  public void startSubscriptions() {
    LCM lcm = LCM.getSingleton();
    // if (hdl32eRayDecoder.hasListeners())
    lcm.subscribe(VelodyneLcmChannels.ray(velodyneModel, lidarId), new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob binaryBlob = new BinaryBlob(ins);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          posDecoder.lasers(byteBuffer);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
    // if (hdl32ePosDecoder.hasListeners())
    lcm.subscribe(VelodyneLcmChannels.pos(velodyneModel, lidarId), new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob binaryBlob = new BinaryBlob(ins);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          posDecoder.positioning(byteBuffer);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
  }
}
