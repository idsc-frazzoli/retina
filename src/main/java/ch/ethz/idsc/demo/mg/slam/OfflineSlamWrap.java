// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// A SLAM algorithm "wrapper" to run the algorithm offline. DVS Events, wheel odometry 
// and lidar pose are provided to the SLAM algorithm
// TODO saveImage as static method in VisualizationUtil
public class OfflineSlamWrap implements OfflineLogListener {
  // listen to DAVIS, lidar and wheel odometry
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final GokartPoseOdometry gokartOdometryPose = GokartPoseOdometry.create();
  private final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  // SLAM algorithm
  private final SlamProvider slamProvider;
  // visualization
  private final SlamMapFrame slamVisualization;
  private boolean isInitialized;
  // frame saving
  private final String imagePrefix;
  private final File parentFilePath;
  private final int savingInterval;
  private int imageCount = 0;
  private int lastTimeStamp;

  public OfflineSlamWrap(PipelineConfig pipelineConfig) {
    slamProvider = new SlamProvider(pipelineConfig, gokartOdometryPose, gokartLidarPose);
    davisDvsDatagramDecoder.addDvsListener(slamProvider);
    slamVisualization = new SlamMapFrame(pipelineConfig);
    imagePrefix = pipelineConfig.logFileName;
    parentFilePath = SlamFileLocations.mapFrames(imagePrefix);
    savingInterval = pipelineConfig.savingInterval.number().intValue();
  }

  // decode DavisDvsEvents, RimoGetEvents and GokartPoseEvents
  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    int timeInst = (int) (1000 * time.number().doubleValue()); // TODO hack
    // in testing, we only start SLAM when lidar pose is available
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
      if (!isInitialized) {
        lastTimeStamp = timeInst;
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
    // every once in a while, run visualization
    if ((timeInst - lastTimeStamp) > savingInterval) {
      // get occurrence map
      slamVisualization.setMap(slamProvider.getMap(0));
      slamVisualization.addGokartPose(gokartLidarPose.getPose());
      saveFrame(slamVisualization.getFrame(), parentFilePath, imagePrefix, timeInst);
      lastTimeStamp = timeInst;
    }
  }

  // for image saving
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
}
