// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.demo.DavisSerial;
import ch.ethz.idsc.retina.dev.davis.DavisApsType;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis.DavisStatics;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsImage;
import ch.ethz.idsc.retina.dev.davis.app.DavisViewerFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsDatagramDecoder;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuLcmDecoder;
import idsc.BinaryBlob;
import idsc.DavisImu;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

/** opens a frame to visualize sensor data from the Davis240c camera
 * which is received via three lcm channels
 * <ul>
 * <li>aps grayscale images
 * <li>dvs events
 * <li>imu
 * </ul> */
public class DavisLcmViewer {
  public static DavisLcmViewer createStandlone(String cameraId, int period) {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisLcmViewer davisLcmViewer = new DavisLcmViewer(cameraId);
    DavisViewerFrame davisViewer = new DavisViewerFrame(davisDevice);
    // handle dvs
    AccumulatedEventsImage accumulatedEventsImage = new AccumulatedEventsImage(davisDevice, period);
    davisLcmViewer.davisDvsDatagramDecoder.addListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisViewer);
    // handle aps
    davisLcmViewer.davisApsDatagramDecoder.addListener(davisViewer);
    // handle aps
    davisLcmViewer.davisRstDatagramDecoder.addListener(davisViewer.rstListener);
    // handle imu
    davisLcmViewer.davisImuLcmDecoder.addListener(davisViewer);
    // start to listen
    davisLcmViewer.subscribe();
    return davisLcmViewer;
  }

  // ---
  private final LCM lcm = LCM.getSingleton();
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final DavisApsDatagramDecoder davisApsDatagramDecoder = new DavisApsDatagramDecoder();
  private final DavisApsDatagramDecoder davisRstDatagramDecoder = new DavisApsDatagramDecoder();
  private final DavisImuLcmDecoder davisImuLcmDecoder = new DavisImuLcmDecoder();
  private final String cameraId;

  private DavisLcmViewer(String cameraId) {
    this.cameraId = cameraId;
  }

  public void subscribe() {
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
    lcm.subscribe(DavisApsBlockPublisher.channel(cameraId, DavisApsType.IMG), new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob apsBinaryBlob = new BinaryBlob(ins);
          ByteBuffer byteBuffer = ByteBuffer.wrap(apsBinaryBlob.data);
          byteBuffer.order(DavisStatics.BYTE_ORDER);
          davisApsDatagramDecoder.decode(byteBuffer);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
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

  public static void main(String[] args) {
    createStandlone(DavisSerial.FX2_02460045.name(), 30_000);
  }
}
