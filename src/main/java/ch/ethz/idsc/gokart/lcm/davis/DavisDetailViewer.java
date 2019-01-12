// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.AbstractAccumulatedImage;
import ch.ethz.idsc.retina.davis.app.AccumulatedEventsGrayImage;
import ch.ethz.idsc.retina.davis.app.DavisImageBuffer;
import ch.ethz.idsc.retina.davis.app.DavisViewerFrame;
import ch.ethz.idsc.retina.davis.app.SignalResetDifference;
import ch.ethz.idsc.retina.util.StartAndStoppable;

public class DavisDetailViewer implements StartAndStoppable {
  private final DavisLcmClient davisLcmClient;
  private final DavisImuLcmClient davisImuLcmClient;
  public final DavisViewerFrame davisViewerFrame;

  public DavisDetailViewer(String cameraId) {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    davisLcmClient = new DavisLcmClient(cameraId);
    AbstractAccumulatedImage abstractAccumulatedImage = AccumulatedEventsGrayImage.of(davisDevice);
    abstractAccumulatedImage.setInterval(25_000);
    davisViewerFrame = new DavisViewerFrame(davisDevice, abstractAccumulatedImage);
    // handle dvs
    davisLcmClient.addDvsListener(abstractAccumulatedImage);
    davisLcmClient.addDvsListener(davisViewerFrame.davisTallyProvider.dvsListener);
    davisLcmClient.addDvsListener(davisViewerFrame.dvsTallyProvider);
    // handle aps
    davisLcmClient.davisSigDatagramDecoder.addListener(davisViewerFrame.davisViewerComponent.sigListener);
    davisLcmClient.davisSigDatagramDecoder.addListener(davisViewerFrame.davisTallyProvider.sigListener);
    // handle aps
    davisLcmClient.davisRstDatagramDecoder.addListener(davisViewerFrame.davisViewerComponent.rstListener);
    davisLcmClient.davisRstDatagramDecoder.addListener(davisViewerFrame.davisTallyProvider.rstListener);
    // handle dif
    DavisImageBuffer davisImageBuffer = new DavisImageBuffer();
    davisLcmClient.davisRstDatagramDecoder.addListener(davisImageBuffer);
    SignalResetDifference signalResetDifference = SignalResetDifference.amplified(davisImageBuffer);
    davisLcmClient.davisSigDatagramDecoder.addListener(signalResetDifference);
    signalResetDifference.addListener(davisViewerFrame.davisViewerComponent.difListener);
    // handle imu
    davisImuLcmClient = new DavisImuLcmClient(cameraId);
    davisImuLcmClient.addListener(davisViewerFrame.davisViewerComponent);
  }

  @Override
  public void start() {
    // start to listen
    davisLcmClient.startSubscriptions();
    davisImuLcmClient.startSubscriptions();
  }

  @Override
  public void stop() {
    davisImuLcmClient.stopSubscriptions();
    davisLcmClient.stopSubscriptions();
    davisViewerFrame.jFrame.setVisible(false);
    davisViewerFrame.jFrame.dispose();
  }
}
