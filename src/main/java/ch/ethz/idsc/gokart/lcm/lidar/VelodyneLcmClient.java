// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashSet;

import ch.ethz.idsc.gokart.lcm.LcmClientInterface;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;
import lcm.lcm.SubscriptionRecord;

/** reference implementation of an lcm client that listens and decodes hdl32e
 * publications and allows listeners to receive the data
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class VelodyneLcmClient implements LcmClientInterface {
  private final VelodyneModel velodyneModel;
  private final VelodyneDecoder velodyneDecoder;
  private final String lidarId;
  private final Collection<SubscriptionRecord> subscriptions = new HashSet<>();

  public VelodyneLcmClient(VelodyneModel velodyneModel, VelodyneDecoder velodyneDecoder, String lidarId) {
    this.velodyneModel = velodyneModel;
    this.velodyneDecoder = velodyneDecoder;
    this.lidarId = lidarId;
  }

  @Override // from LcmClientInterface
  public void startSubscriptions() {
    LCM lcm = LCM.getSingleton();
    if (velodyneDecoder.hasRayListeners())
      subscriptions.add(lcm.subscribe(VelodyneLcmChannels.ray(velodyneModel, lidarId), new LCMSubscriber() {
        @Override
        public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
          try {
            BinaryBlob binaryBlob = new BinaryBlob(ins);
            ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            velodyneDecoder.lasers(byteBuffer);
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      }));
    if (velodyneDecoder.hasPosListeners())
      subscriptions.add(lcm.subscribe(VelodyneLcmChannels.pos(velodyneModel, lidarId), new LCMSubscriber() {
        @Override
        public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
          try {
            BinaryBlob binaryBlob = new BinaryBlob(ins);
            ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            velodyneDecoder.positioning(byteBuffer);
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      }));
  }

  @Override // from LcmClientInterface
  public void stopSubscriptions() {
    LCM.getSingleton().unsubscribeAll(subscriptions);
    subscriptions.clear();
  }
}
