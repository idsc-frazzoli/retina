// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.util.math.Magnitude;

// TODO MG implement DavisDvsListener here instead of SlamSaveFrame --> and use it only for timestamp
public class SlamViewer {
  private final GokartPoseInterface gokartLidarPose;
  private final SlamContainer slamContainer;
  private final SlamMapFrame[] slamMapFrames;
  private final SlamMapGUI slamMapGUI;
  private final SlamSaveFrame slamSaveFrame;
  // ---
  private final Timer timer;
  private final TimerTask visualizationTask;
  private final long visualizationInterval;

  public SlamViewer(SlamConfig slamConfig, SlamContainer slamContainer, GokartPoseInterface gokartLidarPose) {
    this.gokartLidarPose = gokartLidarPose;
    this.slamContainer = slamContainer;
    slamMapGUI = new SlamMapGUI(slamConfig);
    slamMapFrames = new SlamMapFrame[3];
    for (int i = 0; i < slamMapFrames.length; i++)
      slamMapFrames[i] = new SlamMapFrame(slamConfig);
    // ---
    timer = new Timer();
    visualizationInterval = Magnitude.MILLI_SECOND.toLong(slamConfig.visualizationInterval);
    visualizationTask = new TimerTask() {
      @Override
      public void run() {
        visualizationTask();
      }
    };
    timer.schedule(visualizationTask, 0, visualizationInterval);
    slamSaveFrame = new SlamSaveFrame(slamConfig, slamContainer, slamMapFrames);
  }

  private void visualizationTask() {
    slamMapGUI.setFrames(StaticHelper.constructFrames(slamMapFrames, slamContainer, gokartLidarPose));
  }

  // slamSaveFrame requires a DavsDvsEvent stream
  public DavisDvsListener getSlamSaveFrame() {
    return slamSaveFrame;
  }
}
