// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;
import ch.ethz.idsc.demo.mg.util.vis.VisSlamUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

// wrapper class for the SLAM visualization
public class SlamViewer {
  private final SlamVisualization slamVisualization;
  private final SlamMapFrame[] slamMapFrames;
  private final String imagePrefix;
  private final File parentFilePath;
  private final boolean lidarMappingMode;
  private final boolean saveSlamFrame;
  private final double visualizationInterval;
  private final double savingInterval;
  private double lastImagingTimeStamp;
  private double lastSavingTimeStamp;
  private int imageCount;

  public SlamViewer(SlamConfig slamConfig) {
    imagePrefix = slamConfig.davisConfig.logFileName;
    parentFilePath = SlamFileLocations.mapFrames(imagePrefix);
    lidarMappingMode = slamConfig.lidarMappingMode;
    saveSlamFrame = slamConfig.saveSlamFrame;
    visualizationInterval = slamConfig.visualizationInterval.number().doubleValue();
    savingInterval = slamConfig.savingInterval.number().doubleValue();
    slamVisualization = new SlamVisualization(slamConfig);
    slamMapFrames = new SlamMapFrame[3];
    for (int i = 0; i < slamMapFrames.length; i++)
      slamMapFrames[i] = new SlamMapFrame(slamConfig);
  }

  public void initialize(double initTimeStamp) {
    lastImagingTimeStamp = initTimeStamp;
    lastSavingTimeStamp = initTimeStamp;
  }

  public void visualize(SlamProvider slamProvider, GokartPoseInterface gokartLidarPose, double timeStamp) {
    if (saveSlamFrame && ((timeStamp - lastSavingTimeStamp) > savingInterval)) {
      imageCount++;
      BufferedImage slamFrame = VisSlamUtil.constructFrames(slamMapFrames, slamProvider, gokartLidarPose, lidarMappingMode)[1];
      VisGeneralUtil.saveFrame(slamFrame, parentFilePath, imagePrefix, timeStamp, imageCount);
      lastSavingTimeStamp = timeStamp;
    }
    if ((timeStamp - lastImagingTimeStamp) > visualizationInterval) {
      slamVisualization.setFrames(VisSlamUtil.constructFrames(slamMapFrames, slamProvider, gokartLidarPose, lidarMappingMode));
      lastImagingTimeStamp = timeStamp;
    }
  }
}
