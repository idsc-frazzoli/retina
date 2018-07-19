// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.slam.vis.SlamMapFrame;
import ch.ethz.idsc.demo.mg.slam.vis.SlamVisualization;
import ch.ethz.idsc.demo.mg.util.VisGeneralUtil;
import ch.ethz.idsc.demo.mg.util.VisSlamUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

/** A SLAM algorithm "wrapper" to run the algorithm offline. DVS Events, wheel odometry
 * and lidar pose are provided to the SLAM algorithm */
// TODO maybe create abstract wrapper class and then extend OfflineSlamWrap and OfflinePipelineWrap
class OfflineSlamWrap implements OfflineLogListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder;
  private final GokartPoseOdometryDemo gokartOdometryPose;
  private final GokartPoseLcmLidar gokartLidarPose;
  private final SlamProvider slamProvider;
  private final SlamVisualization slamVisualization;
  private final SlamMapFrame[] slamMapFrames;
  private final String imagePrefix;
  private final File parentFilePath;
  private final boolean saveSlamFrame;
  private final boolean lidarMappingMode;
  private final double visualizationInterval;
  private final double savingInterval;
  private boolean isInitialized;
  private double lastImagingTimestamp;
  private double lastSavingTimeStamp;
  private int imageCount;

  public OfflineSlamWrap(SlamConfig slamConfig) {
    davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
    gokartOdometryPose = GokartPoseOdometryDemo.create();
    gokartLidarPose = new GokartPoseLcmLidar();
    slamProvider = new SlamProvider(slamConfig, gokartOdometryPose, gokartLidarPose);
    davisDvsDatagramDecoder.addDvsListener(slamProvider);
    slamVisualization = new SlamVisualization(slamConfig);
    slamMapFrames = new SlamMapFrame[3];
    for (int i = 0; i < slamMapFrames.length; i++)
      slamMapFrames[i] = new SlamMapFrame(slamConfig);
    visualizationInterval = slamConfig.visualizationInterval.number().doubleValue();
    saveSlamFrame = slamConfig.saveSlamFrame;
    lidarMappingMode = slamConfig.lidarMappingMode;
    imagePrefix = slamConfig.davisConfig.logFileName;
    parentFilePath = SlamFileLocations.mapFrames(imagePrefix);
    savingInterval = slamConfig.savingInterval.number().doubleValue();
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    double timeInst = time.number().doubleValue();
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
      if (!isInitialized) {
        lastSavingTimeStamp = timeInst;
        lastImagingTimestamp = timeInst;
        gokartOdometryPose.initializePose(gokartLidarPose.getPose());
        isInitialized = true;
      }
    }
    if (channel.equals("davis240c.overview.dvs")) {
      if (isInitialized)
        davisDvsDatagramDecoder.decode(byteBuffer);
    }
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      gokartOdometryPose.getEvent(new RimoGetEvent(byteBuffer));
    }
    if (saveSlamFrame && ((timeInst - lastSavingTimeStamp) > savingInterval)) {
      imageCount++;
      BufferedImage slamFrame = VisSlamUtil.constructFrames(slamMapFrames, slamProvider, gokartLidarPose, lidarMappingMode)[1];
      VisGeneralUtil.saveFrame(slamFrame, parentFilePath, imagePrefix, timeInst, imageCount);
      lastSavingTimeStamp = timeInst;
    }
    if ((timeInst - lastImagingTimestamp) > visualizationInterval) {
      slamVisualization.setFrames(VisSlamUtil.constructFrames(slamMapFrames, slamProvider, gokartLidarPose, lidarMappingMode));
      lastImagingTimestamp = timeInst;
    }
  }

  public SlamProvider getSlamProvider() {
    return slamProvider;
  }
}
