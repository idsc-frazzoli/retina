// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsGrayImage;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsRgbaImage;
import ch.ethz.idsc.retina.dev.davis.app.DavisImageBuffer;
import ch.ethz.idsc.retina.dev.davis.app.DavisQuickFrame;
import ch.ethz.idsc.retina.dev.davis.app.DavisViewerFrame;
import ch.ethz.idsc.retina.dev.davis.app.SignalResetDifference;

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
    AccumulatedEventsGrayImage accumulatedEventsImage = new AccumulatedEventsGrayImage(davisDevice, period);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(accumulatedEventsImage);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(davisViewerFrame.davisTallyProvider.dvsListener);
    accumulatedEventsImage.addListener(davisViewerFrame.davisViewerComponent.dvsImageListener);
    // handle aps
    davisLcmClient.davisSigDatagramDecoder.addListener(davisViewerFrame.davisViewerComponent.sigListener);
    davisLcmClient.davisSigDatagramDecoder.addListener(davisViewerFrame.davisTallyProvider.sigListener);
    // handle aps
    davisLcmClient.davisRstDatagramDecoder.addListener(davisViewerFrame.davisViewerComponent.rstListener);
    davisLcmClient.davisRstDatagramDecoder.addListener(davisViewerFrame.davisTallyProvider.rstListener);
    // handle dif
    DavisImageBuffer davisImageBuffer = new DavisImageBuffer();
    davisLcmClient.davisRstDatagramDecoder.addListener(davisImageBuffer);
    SignalResetDifference signalResetDifference = new SignalResetDifference(davisImageBuffer);
    davisLcmClient.davisSigDatagramDecoder.addListener(signalResetDifference);
    signalResetDifference.addListener(davisViewerFrame.davisViewerComponent.difListener);
    // handle imu
    davisLcmClient.davisImuLcmDecoder.addListener(davisViewerFrame.davisViewerComponent);
    // start to listen
    davisLcmClient.startSubscriptions();
    // return davisLcmViewer;
  }

  public static void createQuickStandlone(String cameraId, int period) {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisLcmClient davisLcmClient = new DavisLcmClient(cameraId);
    DavisQuickFrame davisViewerFrame = new DavisQuickFrame(davisDevice);
    // handle dvs
    AccumulatedEventsRgbaImage accumulatedEventsImage = new AccumulatedEventsRgbaImage(davisDevice, period);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisViewerFrame.davisViewerComponent.dvsImageListener);
    // handle dif
    DavisImageBuffer davisImageBuffer = new DavisImageBuffer();
    davisLcmClient.davisRstDatagramDecoder.addListener(davisImageBuffer);
    SignalResetDifference signalResetDifference = new SignalResetDifference(davisImageBuffer);
    davisLcmClient.davisSigDatagramDecoder.addListener(signalResetDifference);
    signalResetDifference.addListener(davisViewerFrame.davisViewerComponent.difListener);
    // start to listen
    davisLcmClient.startSubscriptions();
    // return davisLcmViewer;
  }
}
