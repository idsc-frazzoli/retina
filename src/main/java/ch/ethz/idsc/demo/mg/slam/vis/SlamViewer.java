// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// wrapper class for the SLAM visualization
public class SlamViewer implements DavisDvsListener {
  private final GokartPoseInterface gokartLidarPose;
  private final SlamProvider slamProvider;
  private final SlamMapGUI slamMapGUI;
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

  public SlamViewer(SlamConfig slamConfig, SlamProvider slamProvider, GokartPoseInterface gokartLidarPose) {
    this.gokartLidarPose = gokartLidarPose;
    this.slamProvider = slamProvider;
    imagePrefix = slamConfig.davisConfig.logFilename();
    parentFilePath = SlamFileLocations.mapFrames(imagePrefix);
    lidarMappingMode = slamConfig.lidarMappingMode;
    saveSlamFrame = slamConfig.saveSlamFrame;
    visualizationInterval = slamConfig.visualizationInterval.number().doubleValue();
    savingInterval = slamConfig.savingInterval.number().doubleValue();
    slamMapGUI = new SlamMapGUI(slamConfig);
    slamMapFrames = new SlamMapFrame[3];
    for (int i = 0; i < slamMapFrames.length; i++)
      slamMapFrames[i] = new SlamMapFrame(slamConfig);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (slamProvider.getIsInitialized()) {
      double timeStamp = davisDvsEvent.time / 1000000.0;
      if (saveSlamFrame && ((timeStamp - lastSavingTimeStamp) > savingInterval)) {
        imageCount++;
        BufferedImage slamFrame = StaticHelper.constructFrames(slamMapFrames, slamProvider, gokartLidarPose, lidarMappingMode)[1];
        VisGeneralUtil.saveFrame(slamFrame, parentFilePath, imagePrefix, timeStamp, imageCount);
        lastSavingTimeStamp = timeStamp;
      }
      if ((timeStamp - lastImagingTimeStamp) > visualizationInterval) {
        slamMapGUI.setFrames(StaticHelper.constructFrames(slamMapFrames, slamProvider, gokartLidarPose, lidarMappingMode));
        lastImagingTimeStamp = timeStamp;
      }
    }
  }
}
