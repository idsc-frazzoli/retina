// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Objects;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.AbstractAccumulatedImage;
import ch.ethz.idsc.retina.davis.app.AccumulatedEventsGrayImage;
import ch.ethz.idsc.retina.davis.app.DavisImageBuffer;
import ch.ethz.idsc.retina.davis.app.DavisQuickFrame;
import ch.ethz.idsc.retina.davis.app.SignalResetDifference;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

/** contains hard-coded channel names and magic constants */
public class DavisOverviewModule extends AbstractModule {
  private DavisLcmClient davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private DavisQuickFrame davisViewerFrame;
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();

  @Override
  protected void first() throws Exception {
    int period_us = 10_000;
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisLidarComponent davisLidarComponent = new DavisLidarComponent();
    vlp16LcmHandler.lidarAngularFiringCollector.addListener(davisLidarComponent);
    davisViewerFrame = new DavisQuickFrame(davisDevice, davisLidarComponent);
    // handle dvs
    AbstractAccumulatedImage accumulatedEventsImage = AccumulatedEventsGrayImage.of(davisDevice);
    accumulatedEventsImage.setInterval(period_us);
    davisLcmClient.addDvsListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisViewerFrame.davisViewerComponent.dvsImageListener);
    // handle dif
    DavisImageBuffer davisImageBuffer = new DavisImageBuffer();
    davisLcmClient.davisRstDatagramDecoder.addListener(davisImageBuffer);
    SignalResetDifference signalResetDifference = SignalResetDifference.amplified(davisImageBuffer);
    davisLcmClient.davisSigDatagramDecoder.addListener(signalResetDifference);
    signalResetDifference.addListener(davisViewerFrame.davisViewerComponent.difListener);
    // start to listen
    davisLcmClient.startSubscriptions();
    windowConfiguration.attach(getClass(), davisViewerFrame.jFrame);
    davisViewerFrame.jFrame.setVisible(true);
    vlp16LcmHandler.startSubscriptions();
  }

  @Override
  protected void last() {
    vlp16LcmHandler.stopSubscriptions();
    davisLcmClient.stopSubscriptions();
    if (Objects.nonNull(davisViewerFrame)) {
      davisViewerFrame.jFrame.setVisible(false);
      davisViewerFrame.jFrame.dispose();
    }
  }

  public static void standalone() throws Exception {
    DavisOverviewModule davisDetailModule = new DavisOverviewModule();
    davisDetailModule.first();
    davisDetailModule.davisViewerFrame //
        .jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
