// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsGrayImage;
import ch.ethz.idsc.retina.dev.davis.app.DavisImageBuffer;
import ch.ethz.idsc.retina.dev.davis.app.DavisQuickFrame;
import ch.ethz.idsc.retina.dev.davis.app.SignalResetDifference;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class DavisOverviewModule extends AbstractModule {
  private DavisLcmClient davisLcmClient;
  private DavisQuickFrame davisViewerFrame;

  @Override
  protected void first() throws Exception {
    String cameraId = "overview";
    int period_us = 10_000;
    DavisDevice davisDevice = Davis240c.INSTANCE;
    davisLcmClient = new DavisLcmClient(cameraId);
    davisViewerFrame = new DavisQuickFrame(davisDevice);
    // handle dvs
    AccumulatedEventsGrayImage accumulatedEventsImage = new AccumulatedEventsGrayImage(davisDevice, period_us);
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
    davisViewerFrame.jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    davisLcmClient.stopSubscriptions();
    davisViewerFrame.jFrame.setVisible(false);
    davisViewerFrame.jFrame.dispose();
  }
}
