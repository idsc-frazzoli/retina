// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.SlamFileUtil;
import ch.ethz.idsc.demo.mg.util.SlamMapUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// A SLAM algorithm "wrapper" to run the algorithm offline. DVS Events, wheel odometry 
// and lidar pose are provided to the SLAM algorithm
// TODO maybe create abstract wrapper class and then extend OfflineSlamWrap and OfflinePipelineWrap
public class OfflineSlamWrap implements OfflineLogListener {
  // listen to DAVIS, lidar and wheel odometry
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder;
  private final GokartPoseOdometry gokartOdometryPose;
  private final GokartPoseLcmLidar gokartLidarPose;
  // SLAM algorithm
  private final SlamProvider slamProvider;
  // visualization
  private final SlamVisualization slamVisualization;
  private final SlamMapFrame[] slamMapFrames;
  private final int visualizationInterval;
  private boolean isInitialized;
  private int lastImagingTimestamp;
  // frame saving
  private final String imagePrefix;
  private final File parentFilePath;
  private final boolean saveSlamFrame;
  private final int savingInterval;
  private int imageCount;
  private int lastTimeStamp;

  public OfflineSlamWrap(PipelineConfig pipelineConfig) {
    davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
    gokartOdometryPose = GokartPoseOdometry.create();
    gokartLidarPose = new GokartPoseLcmLidar();
    slamProvider = new SlamProvider(pipelineConfig, gokartOdometryPose, gokartLidarPose);
    davisDvsDatagramDecoder.addDvsListener(slamProvider);
    slamVisualization = new SlamVisualization(pipelineConfig);
    slamMapFrames = new SlamMapFrame[3];
    for (int i = 0; i < slamMapFrames.length; i++)
      slamMapFrames[i] = new SlamMapFrame(pipelineConfig);
    visualizationInterval = pipelineConfig.visualizationInterval.number().intValue();
    saveSlamFrame = pipelineConfig.saveSlamFrame;
    imagePrefix = pipelineConfig.logFileName;
    parentFilePath = SlamFileLocations.mapFrames(imagePrefix);
    savingInterval = pipelineConfig.savingInterval.number().intValue();
  }

  // decode DavisDvsEvents, RimoGetEvents and GokartPoseEvents
  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    int timeInst = (int) (1000 * time.number().doubleValue()); // TODO hack
    // we only start SLAM when lidar pose is available
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
      if (!isInitialized) {
        lastTimeStamp = timeInst;
        slamProvider.initialize(gokartLidarPose.getPose(), time.number().doubleValue(), imagePrefix);
        slamVisualization.setFrames(constructFrames());
        isInitialized = true;
      }
    }
    if (channel.equals("davis240c.overview.dvs")) {
      if (isInitialized)
        davisDvsDatagramDecoder.decode(byteBuffer);
    }
    // odometry not required for testing
    // if (channel.equals(RimoLcmServer.CHANNEL_GET))
    // gokartOdometryPose.getEvent(new RimoGetEvent(byteBuffer));
    if (saveSlamFrame && ((timeInst - lastTimeStamp) > savingInterval)) {
      saveFrame(constructFrames()[0], parentFilePath, imagePrefix, timeInst);
      lastTimeStamp = timeInst;
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

  private void saveFrame(BufferedImage bufferedImage, File parentFilePath, String imagePrefix, int timeStamp) {
    try {
      imageCount++;
      String fileName = String.format("%s_%04d_%d.png", imagePrefix, imageCount, timeStamp);
      ImageIO.write(bufferedImage, "png", new File(parentFilePath, fileName));
      System.out.printf("Image saved as %s\n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private BufferedImage[] constructFrames() {
    slamMapFrames[0].setMap(slamProvider.getMap(0));
    slamMapFrames[0].addGokartPose(gokartLidarPose.getPose(), Color.BLACK);
    drawParticlePoses();
    slamMapFrames[0].addGokartPose(slamProvider.getPoseInterface().getPose(), Color.BLUE);
    slamMapFrames[1].setMap(slamProvider.getMap(1));
    slamMapFrames[2].setMap(slamProvider.getMap(2));
    BufferedImage[] combinedFrames = new BufferedImage[3];
    for (int i = 0; i < combinedFrames.length; i++)
      combinedFrames[i] = slamMapFrames[i].getFrame();
    return combinedFrames;
  }

  // draw the pose of particles with highest likelihood
  private void drawParticlePoses() {
    SlamParticle[] slamParticles = slamProvider.getParticles();
    Arrays.sort(slamParticles, SlamMapUtil.slamCompare);
    for (int i = 0; i < 5; i++) {
      slamMapFrames[0].addGokartPose(slamParticles[i].getPose(), Color.RED);
    }
  }
}
