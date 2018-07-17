// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.mg.util.SlamFileUtil;
import ch.ethz.idsc.demo.mg.util.SlamParticleUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

/** A SLAM algorithm "wrapper" to run the algorithm offline. DVS Events, wheel odometry
 * and lidar pose are provided to the SLAM algorithm */
// TODO maybe create abstract wrapper class and then extend OfflineSlamWrap and OfflinePipelineWrap
class OfflineSlamWrap implements OfflineLogListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder;
  private final GokartPoseOdometry gokartOdometryPose;
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
    gokartOdometryPose = GokartPoseOdometry.create();
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
      if (!isInitialized) {
        lastSavingTimeStamp = timeInst;
        lastImagingTimestamp = timeInst;
        isInitialized = true;
      }
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
    }
    if (channel.equals("davis240c.overview.dvs")) {
      if (isInitialized)
        davisDvsDatagramDecoder.decode(byteBuffer);
    }
    // odometry not required for testing
    // if (channel.equals(RimoLcmServer.CHANNEL_GET))
    // gokartOdometryPose.getEvent(new RimoGetEvent(byteBuffer));
    if (saveSlamFrame && ((timeInst - lastSavingTimeStamp) > savingInterval)) {
      saveFrame(constructFrames()[1], parentFilePath, imagePrefix, timeInst);
      lastSavingTimeStamp = timeInst;
    }
    if ((timeInst - lastImagingTimestamp) > visualizationInterval) {
      slamVisualization.setFrames(constructFrames());
      lastImagingTimestamp = timeInst;
    }
  }

  public void saveRecordedMap() {
    SlamFileUtil.saveToCSV(SlamFileLocations.recordedMaps(imagePrefix), slamProvider.getMap(0));
    System.out.println("Slam map successfully saved");
  }

  private void saveFrame(BufferedImage bufferedImage, File parentFilePath, String imagePrefix, double timeStamp) {
    int fileTimeStamp = (int) (1000 * timeStamp);
    try {
      imageCount++;
      String fileName = String.format("%s_%04d_%d.png", imagePrefix, imageCount, fileTimeStamp);
      ImageIO.write(bufferedImage, "png", new File(parentFilePath, fileName));
      System.out.printf("Image saved as %s\n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private BufferedImage[] constructFrames() {
    slamMapFrames[0].setRawMap(slamProvider.getMap(0));
    slamMapFrames[0].addGokartPose(gokartLidarPose.getPose(), Color.BLACK);
    if (!lidarMappingMode)
      drawParticlePoses();
    slamMapFrames[0].addGokartPose(slamProvider.getPoseInterface().getPose(), Color.BLUE);
    slamMapFrames[1].setWayPoints(slamProvider.getWayPoints());
    slamMapFrames[1].addGokartPose(slamProvider.getPoseInterface().getPose(), Color.BLUE);
    BufferedImage[] combinedFrames = new BufferedImage[3];
    for (int i = 0; i < combinedFrames.length; i++)
      combinedFrames[i] = slamMapFrames[i].getFrame();
    return combinedFrames;
  }

  private void drawParticlePoses() {
    SlamParticle[] slamParticles = slamProvider.getParticles();
    int partNumber = slamParticles.length / 3;
    Arrays.sort(slamParticles, 0, partNumber, SlamParticleUtil.SlamCompare);
    for (int i = 0; i < partNumber; i++) {
      slamMapFrames[0].addGokartPose(slamParticles[i].getPose(), Color.RED);
    }
  }
}
