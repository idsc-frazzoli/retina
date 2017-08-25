// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.DavisApsType;
import ch.ethz.idsc.retina.dev.davis.DavisStatics;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsDatagramDecoder;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import idsc.DavisImu;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public class DavisLcmClient implements LcmClientInterface {
  private final String cameraId;
  public final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  public final DavisApsDatagramDecoder davisSigDatagramDecoder = new DavisApsDatagramDecoder();
  public final DavisApsDatagramDecoder davisRstDatagramDecoder = new DavisApsDatagramDecoder();
  public final DavisImuLcmDecoder davisImuLcmDecoder = new DavisImuLcmDecoder();

  public DavisLcmClient(String cameraId) {
    this.cameraId = cameraId;
  }

  @Override
  public void startSubscriptions() {
    LCM lcm = LCM.getSingleton();
    if (davisDvsDatagramDecoder.hasListeners())
      lcm.subscribe(DavisDvsBlockPublisher.channel(cameraId), new LCMSubscriber() {
        @Override
        public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
          try {
            BinaryBlob dvsBinaryBlob = new BinaryBlob(ins);
            ByteBuffer byteBuffer = ByteBuffer.wrap(dvsBinaryBlob.data);
            byteBuffer.order(DavisStatics.BYTE_ORDER);
            davisDvsDatagramDecoder.decode(byteBuffer);
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      });
    if (davisSigDatagramDecoder.hasListeners())
      lcm.subscribe(DavisApsBlockPublisher.channel(cameraId, DavisApsType.SIG), new LCMSubscriber() {
        @Override
        public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
          try {
            BinaryBlob apsBinaryBlob = new BinaryBlob(ins);
            ByteBuffer byteBuffer = ByteBuffer.wrap(apsBinaryBlob.data);
            byteBuffer.order(DavisStatics.BYTE_ORDER);
            davisSigDatagramDecoder.decode(byteBuffer);
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      });
    if (davisRstDatagramDecoder.hasListeners())
      lcm.subscribe(DavisApsBlockPublisher.channel(cameraId, DavisApsType.RST), new LCMSubscriber() {
        @Override
        public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
          try {
            BinaryBlob apsBinaryBlob = new BinaryBlob(ins);
            ByteBuffer byteBuffer = ByteBuffer.wrap(apsBinaryBlob.data);
            byteBuffer.order(DavisStatics.BYTE_ORDER);
            davisRstDatagramDecoder.decode(byteBuffer);
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      });
    if (davisImuLcmDecoder.hasListeners())
      lcm.subscribe(DavisImuFramePublisher.channel(cameraId), new LCMSubscriber() {
        @Override
        public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
          try {
            DavisImu davisImu = new DavisImu(ins);
            davisImuLcmDecoder.decode(davisImu);
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      });
  }
}
