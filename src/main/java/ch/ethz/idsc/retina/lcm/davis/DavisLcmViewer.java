// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsImage;
import ch.ethz.idsc.retina.dev.davis.app.DavisViewerFrame;

/** opens a frame to visualize sensor data from the Davis240c camera which is
 * received via three lcm channels
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
    DavisViewerFrame davisViewerFrame = new DavisViewerFrame(davisDevice);
    // handle dvs
    AccumulatedEventsImage accumulatedEventsImage = new AccumulatedEventsImage(davisDevice, period);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(accumulatedEventsImage);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(davisViewerFrame.davisTallyEventProvider.dvsListener);
    accumulatedEventsImage.addListener(davisViewerFrame);
    // handle aps
    davisLcmClient.davisSigDatagramDecoder.addListener(davisViewerFrame);
    davisLcmClient.davisSigDatagramDecoder.addListener(davisViewerFrame.davisTallyEventProvider.sigListener);
    // handle aps
    davisLcmClient.davisRstDatagramDecoder.addListener(davisViewerFrame.rstListener);
    davisLcmClient.davisRstDatagramDecoder.addListener(davisViewerFrame.davisTallyEventProvider.rstListener);
    // handle imu
    davisLcmClient.davisImuLcmDecoder.addListener(davisViewerFrame);
    // start to listen
    davisLcmClient.startSubscriptions();
    // return davisLcmViewer;
  }
}
