// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsGrayImage;
import ch.ethz.idsc.retina.dev.davis.app.DavisImageBuffer;
import ch.ethz.idsc.retina.dev.davis.app.DavisViewerFrame;
import ch.ethz.idsc.retina.dev.davis.app.SignalResetDifference;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class DavisDetailModule extends AbstractModule {
  private DavisLcmClient davisLcmClient;
  private DavisViewerFrame davisViewerFrame;

  @Override
  protected void first() throws Exception {
    String cameraId = "overview";
    int period_us = 10_000;
    // ---
    DavisDevice davisDevice = Davis240c.INSTANCE;
    davisLcmClient = new DavisLcmClient(cameraId);
    davisViewerFrame = new DavisViewerFrame(davisDevice);
    // handle dvs
    AccumulatedEventsGrayImage accumulatedEventsImage = new AccumulatedEventsGrayImage(davisDevice, period_us);
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
  }

  @Override
  protected void last() {
    davisLcmClient.stopSubscriptions();
    davisViewerFrame.jFrame.setVisible(false);
    davisViewerFrame.jFrame.dispose();
  }

  public static void main(String[] args) throws Exception {
    new DavisDetailModule().first();
  }
}
