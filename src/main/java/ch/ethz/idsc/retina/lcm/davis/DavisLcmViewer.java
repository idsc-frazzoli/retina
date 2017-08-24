// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import ch.ethz.idsc.retina.demo.DavisSerial;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsImage;
import ch.ethz.idsc.retina.dev.davis.app.DavisViewerFrame;

/** opens a frame to visualize sensor data from the Davis240c camera
 * which is received via three lcm channels
 * <ul>
 * <li>aps grayscale images
 * <li>dvs events
 * <li>imu
 * </ul> */
public enum DavisLcmViewer {
  ;
  public static void createStandlone(String cameraId, int period) {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisLcmClient davisLcmClient = new DavisLcmClient(cameraId);
    DavisViewerFrame davisViewer = new DavisViewerFrame(davisDevice);
    // handle dvs
    AccumulatedEventsImage accumulatedEventsImage = new AccumulatedEventsImage(davisDevice, period);
    davisLcmClient.davisDvsDatagramDecoder.addListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisViewer);
    // handle aps
    davisLcmClient.davisSigDatagramDecoder.addListener(davisViewer);
    // handle aps
    davisLcmClient.davisRstDatagramDecoder.addListener(davisViewer.rstListener);
    // handle imu
    davisLcmClient.davisImuLcmDecoder.addListener(davisViewer);
    // start to listen
    davisLcmClient.startSubscriptions();
    // return davisLcmViewer;
  }

  public static void main(String[] args) {
    createStandlone(DavisSerial.FX2_02460045.name(), 30_000);
  }
}
